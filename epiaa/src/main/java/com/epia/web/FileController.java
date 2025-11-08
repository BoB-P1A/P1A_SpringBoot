package com.epia.web;
import com.epia.storage.FileStorageService;
import com.epia.storage.S3FileStorageService;
import com.epia.support.ApiException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
public class FileController {
    private final FileStorageService storage;
    private final S3FileStorageService s3Storage;

    public FileController(FileStorageService s, S3FileStorageService s3Storage) {
        this.storage = s;
        this.s3Storage = s3Storage;
    }

    // POST /files/upload (multipart form-data: file, folder)
    @PostMapping(value="/files/upload", consumes={"multipart/form-data"})
    public Map<String,Object> upload(
            @RequestPart("file") MultipartFile file,
            @RequestPart(value="folder",required=false) String folder) {
        var s=storage.save(file, folder==null?"checklist":folder);
        return Map.of("fileUrl", s.fileUrl(), "fileName", s.fileName(), "fileSize", s.fileSize(), "contentType", s.contentType());
    }

    // POST /files/upload/technical - S3 업로드 (기술적 보호조치 전용)
    @PostMapping(value="/files/upload/technical", consumes={"multipart/form-data"})
    public Map<String,Object> uploadTechnical(
            @RequestPart("file") MultipartFile file,
            @RequestPart("companyId") String companyId,
            @RequestPart("category") String category,
            @RequestPart("systemId") String systemId,
            @RequestPart("no") String no) {

        System.out.println("POST /files/upload/technical - companyId: " + companyId +
                ", category: " + category +
                ", systemId: " + systemId +
                ", no: " + no +
                ", filename: " + file.getOriginalFilename());

        try {
            var result = s3Storage.uploadTechnical(file, companyId, category, systemId, no);
            return Map.of(
                    "fileUrl", result.fileUrl(),
                    "fileName", result.fileName(),
                    "fileSize", result.fileSize(),
                    "contentType", result.contentType()
            );
        } catch (IllegalArgumentException e) {
            // 확장자 검증 오류 등 - 400 에러로 변환
            throw new ApiException(400, e.getMessage());
        } catch (RuntimeException e) {
            // S3 업로드 오류 등 - 500 에러로 변환
            System.err.println("파일 업로드 실패: " + e.getMessage());
            throw new ApiException(500, e.getMessage());
        }
    }

    // GET /files/download - Pre-signed URL 생성
    @GetMapping("/files/download")
    public Map<String, String> getDownloadUrl(@RequestParam String fileUrl) {
        System.out.println("GET /files/download - fileUrl: " + fileUrl);

        try {
            // S3 URL인지 확인
            if (fileUrl == null || !fileUrl.contains(".s3.")) {
                throw new ApiException(400, "유효하지 않은 파일 URL입니다");
            }

            String s3Key = s3Storage.extractS3KeyFromUrl(fileUrl);
            String presignedUrl = s3Storage.generatePresignedUrl(s3Key);

            return Map.of("downloadUrl", presignedUrl);
        } catch (IllegalArgumentException e) {
            throw new ApiException(400, e.getMessage());
        } catch (Exception e) {
            System.err.println("다운로드 URL 생성 실패: " + e.getMessage());
            throw new ApiException(500, "다운로드 URL 생성 실패: " + e.getMessage());
        }
    }

    // DELETE /files  { "fileUrl": "..." }
    @DeleteMapping("/files")
    public Map<String,String> delete(@RequestBody Map<String,String> b){
        String fileUrl = b.get("fileUrl");

        if (fileUrl == null || fileUrl.isEmpty()) {
            throw new ApiException(400, "파일 URL이 제공되지 않았습니다");
        }

        boolean ok;

        // S3 URL인지 확인
        if (fileUrl.contains(".s3.")) {
            ok = s3Storage.deleteByUrl(fileUrl);
        } else {
            ok = storage.deleteByUrl(fileUrl);
        }

        if (!ok) {
            throw new ApiException(500, "파일 삭제에 실패했습니다");
        }

        return Map.of("message", "파일이 삭제되었습니다");
    }
}