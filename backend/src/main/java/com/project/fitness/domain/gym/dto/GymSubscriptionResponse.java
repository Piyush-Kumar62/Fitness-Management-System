package com.project.fitness.domain.gym.dto;
import com.project.fitness.domain.gym.model.Gym;

import com.project.fitness.domain.gym.model.GymSubscriptionStatus;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GymSubscriptionResponse {

  private String id;
  private String gymId;
  private String gymName;
  private String planId;
  private String planName;
  private LocalDate startDate;
  private LocalDate endDate;
  private GymSubscriptionStatus status;
  private boolean autoRenew;
}
