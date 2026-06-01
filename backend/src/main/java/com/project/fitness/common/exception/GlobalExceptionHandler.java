package com.project.fitness.common.exception;

import com.project.fitness.common.response.ApiResponse;
import com.project.fitness.ai.exception.AiProviderException;
import com.project.fitness.common.util.CorrelationIdUtil;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Global exception handler. Every response follows the standard {@link ApiResponse} envelope.
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  // 1. Validation errors (@NotBlank, @Size, etc.)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleValidation(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String field = ((FieldError) error).getField();
      errors.put(field, error.getDefaultMessage());
    });
    return ResponseEntity.badRequest()
        .body(ApiResponse.error("Validation failed: " + errors, correlationId()));
  }

  // 2. Resource not found
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
    log.warn("[{}] Resource not found: {}", correlationId(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiResponse.error(ex.getMessage(), correlationId()));
  }

  // 3. Bad request
  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ApiResponse<Void>> handleBadRequest(BadRequestException ex) {
    log.warn("[{}] Bad request: {}", correlationId(), ex.getMessage());
    return ResponseEntity.badRequest()
        .body(ApiResponse.error(ex.getMessage(), correlationId()));
  }

  // 4. Unauthorized (custom)
  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ApiResponse<Void>> handleUnauthorized(UnauthorizedException ex) {
    log.warn("[{}] Unauthorized: {}", correlationId(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ApiResponse.error(ex.getMessage(), correlationId()));
  }

  // 5. Bad credentials
  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
    log.warn("[{}] Bad credentials", correlationId());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ApiResponse.error("Invalid email or password", correlationId()));
  }

  // 6. Access denied (Spring Security RBAC)
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
    log.warn("[{}] Access denied: {}", correlationId(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(ApiResponse.error("Access denied", correlationId()));
  }

  // 7. Illegal argument
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<Void>> handleIllegalArg(IllegalArgumentException ex) {
    log.warn("[{}] Illegal argument: {}", correlationId(), ex.getMessage());
    return ResponseEntity.badRequest()
        .body(ApiResponse.error(ex.getMessage(), correlationId()));
  }

  @ExceptionHandler(AiProviderException.class)
  public ResponseEntity<ApiResponse<Void>> handleAiProvider(AiProviderException ex) {
    log.error("[{}] AI provider error: {}", correlationId(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
        .body(ApiResponse.error("AI provider unavailable", correlationId()));
  }

  // 8. Generic runtime
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ApiResponse<Void>> handleRuntime(RuntimeException ex) {
    log.error("[{}] Unexpected runtime error", correlationId(), ex);
    return ResponseEntity.internalServerError()
        .body(ApiResponse.error("An unexpected error occurred", correlationId()));
  }

  // 8.2 Malformed JSON/body parse errors
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponse<Void>> handleUnreadableMessage(HttpMessageNotReadableException ex) {
    log.warn("[{}] Malformed request body: {}", correlationId(), ex.getMessage());
    return ResponseEntity.badRequest()
        .body(ApiResponse.error("Malformed request body", correlationId()));
  }

  // 8.5 Missing static/resource route
  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ApiResponse<Void>> handleNoResource(NoResourceFoundException ex) {
    log.warn("[{}] Resource not found: {}", correlationId(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiResponse.error("Requested resource was not found", correlationId()));
  }

  // 9. Catch-all
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleGlobal(Exception ex) {
    log.error("[{}] Unhandled exception", correlationId(), ex);
    return ResponseEntity.internalServerError()
        .body(ApiResponse.error("An error occurred processing your request", correlationId()));
  }

  private String correlationId() {
    return CorrelationIdUtil.get();
  }
}
