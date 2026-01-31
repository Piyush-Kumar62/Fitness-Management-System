package com.project.fitness.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneResponse {
  private String id;
  private String goalId;
  private String title;
  private String description;
  private Double targetValue;
  private Boolean achieved;
  private LocalDateTime achievedAt;
  private LocalDateTime createdAt;
}
