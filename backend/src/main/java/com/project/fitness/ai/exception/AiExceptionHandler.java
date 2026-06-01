package com.project.fitness.ai.exception;

import com.project.fitness.common.response.ApiResponse;
import com.project.fitness.common.util.CorrelationIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.project.fitness.ai")
@Slf4j
public class AiExceptionHandler {

  @ExceptionHandler(AiValidationException.class)
  public ResponseEntity<ApiResponse<Void>> handleValidation(AiValidationException ex) {
    log.warn("[{}] AI validation error: {}", correlationId(), ex.getMessage());
    return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage(), correlationId()));
  }

  @ExceptionHandler(AiRateLimitException.class)
  public ResponseEntity<ApiResponse<Void>> handleRateLimit(AiRateLimitException ex) {
    log.warn("[{}] AI rate limit: {}", correlationId(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
        .body(ApiResponse.error(ex.getMessage(), correlationId()));
  }

  @ExceptionHandler(AiProviderException.class)
  public ResponseEntity<ApiResponse<Void>> handleProvider(AiProviderException ex) {
    log.error("[{}] AI provider error: {}", correlationId(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
        .body(ApiResponse.error("AI provider unavailable", correlationId()));
  }

  @ExceptionHandler(AiException.class)
  public ResponseEntity<ApiResponse<Void>> handleAiException(AiException ex) {
    log.error("[{}] AI error: {}", correlationId(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.error("AI service error", correlationId()));
  }

  private String correlationId() {
    return CorrelationIdUtil.get();
  }
}
