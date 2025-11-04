package com.epia.support;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
  record Err(int status, String message) {}
  @ExceptionHandler(ApiException.class) public ResponseEntity<?> api(ApiException e){
    return ResponseEntity.status(e.status).body(new Err(e.status,e.getMessage()));
  }
  @ExceptionHandler(Exception.class) public ResponseEntity<?> etc(Exception e){
    e.printStackTrace(); return ResponseEntity.status(500).body(new Err(500,"서버 오류"));
  }
}