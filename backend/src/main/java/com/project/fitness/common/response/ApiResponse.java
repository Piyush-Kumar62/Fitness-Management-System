package com.project.fitness.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

// Standard API envelope: { success, message, data, timestamp, correlationId }. All controllers return this type for consistency.
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

  private final boolean success;
  private final String message;
  private final T data;
  private final String timestamp;
  private final String correlationId;

  // Convenience factory – success with data.
  public static <T> ApiResponse<T> success(T data) {
    return ApiResponse.<T>builder()
        .success(true)
        .message("OK")
        .data(data)
        .timestamp(Instant.now().toString())
        .build();
  }

  // Convenience factory – success with data + message.
  public static <T> ApiResponse<T> success(T data, String message) {
    return ApiResponse.<T>builder()
        .success(true)
        .message(message)
        .data(data)
        .timestamp(Instant.now().toString())
        .build();
  }

  // Convenience factory – error without data.
  public static <T> ApiResponse<T> error(String message, String correlationId) {
    return ApiResponse.<T>builder()
        .success(false)
        .message(message)
        .timestamp(Instant.now().toString())
        .correlationId(correlationId)
        .build();
  }
}
