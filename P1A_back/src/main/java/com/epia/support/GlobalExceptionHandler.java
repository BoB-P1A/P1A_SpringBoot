package com.epia.support;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
  record Err(int status, String message) {}
  @ExceptionHandler(ApiException.class) public ResponseEntity<?> api(ApiException e){
    return ResponseEntity.status(e.status).body(new Err(e.status,e.getMessage()));
  }

  // IllegalArgumentException 처리 (404 Not Found)
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> illegalArgument(IllegalArgumentException e) {
      return ResponseEntity.status(404).body(new Err(404, e.getMessage()));
  }

  // Validation 예외 처리 (400 Bad Request)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> validation(MethodArgumentNotValidException ex) {
      Map<String, String> errors = new HashMap<>();
      ex.getBindingResult().getAllErrors().forEach((error) -> {
          String fieldName = ((FieldError) error).getField();
          String errorMessage = error.getDefaultMessage();
          errors.put(fieldName, errorMessage);
      });
      return ResponseEntity.status(400).body(errors);
  }

  @ExceptionHandler(Exception.class) public ResponseEntity<?> etc(Exception e){
    e.printStackTrace(); return ResponseEntity.status(500).body(new Err(500,"서버 오류"));
  }
}