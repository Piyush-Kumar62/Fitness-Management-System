package com.project.fitness.dto;

import com.project.fitness.model.GoalStatus;
import com.project.fitness.model.GoalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalRequest {
  @NotBlank(message = "Title is required")
  private String title;
  
  private String description;
  
  @NotNull(message = "Goal type is required")
  private GoalType type;
  
  private Double targetValue;
  private Double currentValue;
  
  @NotBlank(message = "Unit is required")
  private String unit;
  
  private LocalDate startDate;
  private LocalDate deadline;
  private GoalStatus status;
}
