package com.epia.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class S3FileStorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    // 허용된 파일 확장자 목록
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "pptx", "doc", "docx", "bmp", "gif", "hwp",
            "jpeg", "jpg", "pdf", "png", "txt", "zip"
    );

    // 최대 파일 크기 (500MB)
    private static final long MAX_FILE_SIZE = 500 * 1024 * 1024;

    public S3FileStorageService(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    /**
     * 기술적 보호조치 파일 업로드
     * 경로: /companyId/개인정보처리시스템(Admin)/systemName/no/파일명
     */
    public FileUploadResult uploadTechnical(
            MultipartFile file,
            String companyId,
            String category,
            String systemId,
            String no
    ) {
        try {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                originalFilename = "unnamed_file";
            }

            // 파일 확장자 검증
            validateFileExtension(originalFilename);

            // 중복 방지를 위해 UUID 추가
            String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;

            // S3 경로 구성: companyId/category/systemId/no/파일명
            String s3Key = String.format("%s/%s/%s/%s/%s",
                    companyId,
                    category,
                    systemId,
                    no,
                    uniqueFilename
            );

            // S3에 파일 업로드 (비공개로 저장 - ACL 설정 안 함)
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    // ACL 설정 제거 → 기본적으로 비공개
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            // 원본 URL 생성 (Pre-signed URL 아님)
            String fileUrl = generateS3Url(s3Key);

            return new FileUploadResult(fileUrl, originalFilename, file.getSize(), file.getContentType());

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다: " + e.getMessage(), e);
        } catch (S3Exception e) {
            throw new RuntimeException("S3 업로드 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * Pre-signed URL 생성 (1시간 유효)
     * 원본 파일명으로 다운로드되도록 Content-Disposition 설정
     * 이 메서드는 다운로드 시에만 호출됩니다
     */
    public String generatePresignedUrl(String s3Key) {
        try {
            // S3 key에서 원본 파일명 추출
            String originalFileName = extractOriginalFileName(s3Key);

            // 파일명 URL 인코딩 (RFC 5987 형식)
            String encodedFileName = URLEncoder.encode(originalFileName, StandardCharsets.UTF_8)
                    .replace("+", "%20");  // 공백을 %20으로 변환

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    // RFC 5987 형식으로 Content-Disposition 설정
                    .responseContentDisposition("attachment; filename*=UTF-8''" + encodedFileName)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofHours(1))  // 1시간 유효
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            return presignedRequest.url().toString();
        } catch (S3Exception e) {
            throw new RuntimeException("Pre-signed URL 생성 실패: " + e.getMessage(), e);
        }
    }

    /**
     * S3 key에서 원본 파일명 추출
     */
    private String extractOriginalFileName(String s3Key) {
        // 마지막 "/" 이후의 파일명 추출
        String fileName = s3Key.substring(s3Key.lastIndexOf('/') + 1);

        // UUID 부분 제거 (첫 번째 "_" 이후가 원본 파일명)
        int underscoreIndex = fileName.indexOf('_');
        if (underscoreIndex != -1 && underscoreIndex < fileName.length() - 1) {
            return fileName.substring(underscoreIndex + 1);
        }

        // UUID가 없으면 전체 파일명 반환
        return fileName;
    }

    /**
     * S3 URL 생성 (간단한 버전)
     */
    private String generateS3Url(String s3Key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, s3Key);
    }

    /**
     * S3 URL에서 key 추출 - URL 디코딩 포함
     */
    public String extractS3KeyFromUrl(String fileUrl) {
        // 패턴 1: https://버킷명.s3.리전.amazonaws.com/key
        String pattern1 = String.format("https://%s.s3.%s.amazonaws.com/", bucketName, region);

        // 패턴 2: https://s3.리전.amazonaws.com/버킷명/key
        String pattern2 = String.format("https://s3.%s.amazonaws.com/%s/", region, bucketName);

        // 패턴 3: https://버킷명.s3.amazonaws.com/key (리전 없음)
        String pattern3 = String.format("https://%s.s3.amazonaws.com/", bucketName);

        String encodedKey = null;

        if (fileUrl.startsWith(pattern1)) {
            encodedKey = fileUrl.substring(pattern1.length());
        } else if (fileUrl.startsWith(pattern2)) {
            encodedKey = fileUrl.substring(pattern2.length());
        } else if (fileUrl.startsWith(pattern3)) {
            encodedKey = fileUrl.substring(pattern3.length());
        } else {
            throw new IllegalArgumentException("유효하지 않은 S3 URL입니다: " + fileUrl);
        }

        // URL 디코딩 (한글 경로 처리)
        try {
            return java.net.URLDecoder.decode(encodedKey, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("URL 디코딩 실패, 원본 key 사용: " + encodedKey);
            return encodedKey;
        }
    }

    /**
     * 파일 크기 검증
     */
    private void validateFileSize(long fileSize) {
        if (fileSize > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                    String.format("파일 크기가 너무 큽니다. 최대 %dMB까지 업로드 가능합니다.",
                            MAX_FILE_SIZE / (1024 * 1024))
            );
        }
    }

    /**
     * 파일 확장자 검증
     */
    private void validateFileExtension(String filename) {
        String extension = getFileExtension(filename);

        if (extension == null || extension.isEmpty()) {
            throw new IllegalArgumentException("파일 확장자가 없습니다.");
        }

        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException(
                    String.format("허용되지 않은 파일 형식입니다. 허용된 확장자: %s",
                            String.join(", ", ALLOWED_EXTENSIONS))
            );
        }
    }

    /**
     * 파일명에서 확장자 추출
     */
    private String getFileExtension(String filename) {
        if (filename == null) {
            return null;
        }

        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return null;
        }

        return filename.substring(lastDotIndex + 1);
    }

    /**
     * S3에서 파일 삭제
     */
    public boolean deleteByUrl(String fileUrl) {
        try {
            String s3Key = extractS3KeyFromUrl(fileUrl);

            // 파일 존재 확인
            try {
                HeadObjectRequest headRequest = HeadObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Key)
                        .build();
                s3Client.headObject(headRequest);
            } catch (NoSuchKeyException e) {
                System.err.println("S3 파일 없음: " + s3Key);
                return false;
            }

            // 삭제 실행
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            System.out.println("S3 파일 삭제 완료: " + s3Key);

            return true;

        } catch (IllegalArgumentException e) {
            System.err.println("URL 파싱 실패: " + e.getMessage());
            return false;
        } catch (S3Exception e) {
            System.err.println("S3 삭제 실패: " + e.awsErrorDetails().errorMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("파일 삭제 오류: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 파일 업로드 결과 record
     */
    public record FileUploadResult(String fileUrl, String fileName, long fileSize, String contentType) {}

}
