package com.project.fitness.domain.membership.dto;
import com.project.fitness.domain.membership.model.Membership;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignPlanRequest {

  @NotBlank(message = "Member ID is required")
  private String memberId;

  private String workoutPlanId;
  private String dietPlanId;
}
