package com.project.fitness.domain.trainer.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateClassRequest {

  @NotBlank(message = "Class name is required")
  private String className;

  @NotNull(message = "Start time is required")
  @Future(message = "Start time must be in future")
  private LocalDateTime startTime;

  @NotNull(message = "End time is required")
  @Future(message = "End time must be in future")
  private LocalDateTime endTime;

  @Min(value = 1, message = "Capacity must be at least 1")
  private int capacity;
}
