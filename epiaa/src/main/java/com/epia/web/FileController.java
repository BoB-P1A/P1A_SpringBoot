package com.epia.web;
import com.epia.storage.FileStorageService; 
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController public class FileController {
  private final FileStorageService storage; public FileController(FileStorageService s){ this.storage=s; }

  // POST /files/upload (multipart form-data: file, folder)
  @PostMapping(value="/files/upload", consumes={"multipart/form-data"})
  public Map<String,Object> upload(@RequestPart("file") MultipartFile file,@RequestPart(value="folder",required=false) String folder){
    var s=storage.save(file, folder==null?"checklist":folder);
    return Map.of("fileUrl", s.fileUrl(), "fileName", s.fileName(), "fileSize", s.fileSize(), "contentType", s.contentType());
  }

  // DELETE /files  { "fileUrl": "..." }
  @DeleteMapping("/files") public Map<String,String> delete(@RequestBody Map<String,String> b){
    boolean ok=storage.deleteByUrl(b.get("fileUri")); return Map.of("message", ok ? "파일이 삭제되었습니다" : "삭제할 파일이 없습니다");
  }
}