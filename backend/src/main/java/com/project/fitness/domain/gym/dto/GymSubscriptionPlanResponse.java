package com.project.fitness.domain.gym.dto;
import com.project.fitness.domain.gym.model.Gym;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GymSubscriptionPlanResponse {

  private String id;
  private String name;
  private BigDecimal monthlyPrice;
  private int maxMembers;
  private int maxTrainers;
  private String features;
  private boolean active;
}
