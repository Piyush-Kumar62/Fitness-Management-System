package com.project.fitness.domain.fitness.dto;

import com.project.fitness.domain.fitness.model.ActivityType;
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
public class ActivityResponse {
  private String id;
  private String userId;
  private ActivityType type;
  private Map<String, Object> additionalMetrics;
  private Integer duration;
  private Integer caloriesBurned;
  private LocalDateTime startTime;
  private LocalDateTime date;
  private Double distance;
  private String intensity;
  private String notes;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
