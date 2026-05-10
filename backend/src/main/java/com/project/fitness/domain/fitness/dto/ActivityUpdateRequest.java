package com.project.fitness.domain.fitness.dto;

import com.project.fitness.domain.fitness.model.ActivityType;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityUpdateRequest {
  private ActivityType type;
  private Integer duration;
  private Integer caloriesBurned;
  private LocalDateTime startTime;
  private LocalDateTime date;
  private Double distance;
  private String intensity;
  private String notes;
  private Map<String, Object> additionalMetrics;
}
