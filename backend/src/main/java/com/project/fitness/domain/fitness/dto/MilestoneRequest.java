package com.project.fitness.domain.fitness.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneRequest {
  private String title;
  private String description;
  private Double targetValue;
}
