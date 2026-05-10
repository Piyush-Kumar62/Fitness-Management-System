package com.project.fitness.domain.fitness.dto;

import com.project.fitness.domain.fitness.model.ActivityType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActivityStatisticsResponse {
  private long totalActivities;
  private long totalDuration;
  private long totalCalories;
  private double averageDuration;
  private double averageCalories;
  private ActivityType mostCommonType;
}
