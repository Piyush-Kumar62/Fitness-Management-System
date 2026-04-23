package com.project.fitness.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Standardized error response DTO for consistent API error formatting.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

  private int status;
  private String error;
  private String message;
  private String path;

  @Builder.Default
  private LocalDateTime timestamp = LocalDateTime.now();

  private List<FieldError> fieldErrors;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class FieldError {
    private String field;
    private String message;
  }
}
