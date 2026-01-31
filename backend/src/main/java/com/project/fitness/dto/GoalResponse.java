package com.project.fitness.dto;

import com.project.fitness.model.GoalStatus;
import com.project.fitness.model.GoalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalResponse {
  private String id;
  private String userId;
  private String title;
  private String description;
  private GoalType type;
  private Double targetValue;
  private Double currentValue;
  private String unit;
  private LocalDate startDate;
  private LocalDate deadline;
  private GoalStatus status;
  private Double progressPercentage;
  private List<MilestoneResponse> milestones;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
