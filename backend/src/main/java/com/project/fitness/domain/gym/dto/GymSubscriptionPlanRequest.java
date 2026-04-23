package com.project.fitness.domain.gym.dto;
import com.project.fitness.domain.gym.model.Gym;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GymSubscriptionPlanRequest {

  @NotBlank(message = "Plan name is required")
  private String name;

  @NotNull(message = "Monthly price is required")
  @DecimalMin(value = "0.0", inclusive = false, message = "Monthly price must be greater than 0")
  private BigDecimal monthlyPrice;

  @Min(value = 1, message = "Max members must be at least 1")
  private int maxMembers;

  @Min(value = 1, message = "Max trainers must be at least 1")
  private int maxTrainers;

  @Size(max = 1000, message = "Features can be at most 1000 characters")
  private String features;
}
