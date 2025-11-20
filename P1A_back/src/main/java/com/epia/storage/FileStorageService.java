package com.epia.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
  record Stored(String fileUrl,String fileName,long fileSize,String contentType) {}
  Stored save(MultipartFile file,String folder);
  boolean deleteByUrl(String fileUrl);
}