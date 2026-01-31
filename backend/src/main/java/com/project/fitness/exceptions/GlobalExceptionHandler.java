package com.project.fitness.exceptions;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  // 1. Handle Validation Errors (e.g., @NotBlank, @Size in your DTOs)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();

    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  // 2. Handle Resource Not Found
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Map<String, String>> handleResourceNotFoundException(
      ResourceNotFoundException ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", ex.getMessage());
    logger.warn("Resource not found: {}", ex.getMessage());
    return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
  }

  // 3. Handle Bad Request
  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<Map<String, String>> handleBadRequestException(
      BadRequestException ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", ex.getMessage());
    logger.warn("Bad request: {}", ex.getMessage());
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  // 4. Handle Unauthorized
  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<Map<String, String>> handleUnauthorizedException(
      UnauthorizedException ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", ex.getMessage());
    logger.warn("Unauthorized access attempt: {}", ex.getMessage());
    return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
  }

  // 5. Handle Bad Credentials
  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<Map<String, String>> handleBadCredentialsException(
      BadCredentialsException ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", "Invalid email or password");
    logger.warn("Failed login attempt");
    return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
  }

  // 6. Handle Access Denied
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Map<String, String>> handleAccessDeniedException(
      AccessDeniedException ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", "Access denied");
    logger.warn("Access denied: {}", ex.getMessage());
    return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
  }

  // 7. Handle IllegalArgumentException
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, String>> handleIllegalArgumentException(
      IllegalArgumentException ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", ex.getMessage());
    logger.warn("Illegal argument: {}", ex.getMessage());
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  // 8. Generic Handler for all other runtime errors
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", "An unexpected error occurred");
    // Don't expose internal error details to clients
    logger.error("Unexpected error occurred", ex);
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  // 9. Generic catch-all for any Exception
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, String>> handleGlobalException(Exception ex) {
    Map<String, String> error = new HashMap<>();
    error.put("error", "An error occurred processing your request");
    logger.error("Unexpected exception", ex);
    return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}