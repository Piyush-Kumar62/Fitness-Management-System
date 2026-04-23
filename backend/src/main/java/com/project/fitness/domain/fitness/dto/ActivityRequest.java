package com.project.fitness.domain.fitness.dto;
import com.project.fitness.domain.fitness.model.Activity;

import com.project.fitness.domain.fitness.model.ActivityType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityRequest {
  private String userId;
  
  @NotNull(message = "Activity type is required")
  private ActivityType type;
  
  private Map<String, Object> additionalMetrics;
  
  @NotNull(message = "Duration is required")
  @Min(value = 1, message = "Duration must be at least 1 minute")
  private Integer duration;
  
  @NotNull(message = "Calories burned is required")
  @Min(value = 0, message = "Calories burned cannot be negative")
  private Integer caloriesBurned;
  
  private LocalDateTime startTime;
  private LocalDateTime date;
  private Double distance; // in km
  private String intensity;
  private String notes;

}
