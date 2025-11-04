package com.epia.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.*;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService {
  private final Path base; public LocalFileStorageService(@Value("${app.files.local-base-dir}") String baseDir){ this.base=Path.of(baseDir); }
  
  @Override
  public Stored save(MultipartFile file,String folder){
    try{
      Path dir=base.resolve(folder==null?"":folder); Files.createDirectories(dir);
      String name=UUID.randomUUID()+"_"+file.getOriginalFilename(); Path p=dir.resolve(name); file.transferTo(p.toFile());
      return new Stored(p.toAbsolutePath().toString(), file.getOriginalFilename(), file.getSize(), file.getContentType());
    }catch(Exception e){ throw new RuntimeException(e); }
  }
  
  @Override
  public boolean deleteByUrl(String fileUrl){
    try{ return Files.deleteIfExists(Path.of(fileUrl)); }catch(Exception e){ return false; }
  }
}