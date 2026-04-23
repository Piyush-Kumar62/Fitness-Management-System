package com.project.fitness.domain.gym.dto;
import com.project.fitness.domain.gym.model.Gym;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GymSubscribeRequest {

  @NotBlank(message = "Gym ID is required")
  private String gymId;

  @NotBlank(message = "Plan ID is required")
  private String planId;
}
