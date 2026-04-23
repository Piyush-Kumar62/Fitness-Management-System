package com.project.fitness.domain.membership.dto;
import com.project.fitness.domain.gym.model.Gym;
import com.project.fitness.domain.membership.model.Membership;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class MembershipPlanRequest {

  @NotBlank(message = "Gym ID is required")
  private String gymId;

  @NotBlank(message = "Plan name is required")
  @Size(max = 100, message = "Plan name can be at most 100 characters")
  private String name;

  @NotNull(message = "Price is required")
  @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
  private BigDecimal price;

  @NotNull(message = "Duration is required")
  @Positive(message = "Duration must be greater than 0")
  private Integer durationDays;

  @Size(max = 1000, message = "Features can be at most 1000 characters")
  private String features;
}
