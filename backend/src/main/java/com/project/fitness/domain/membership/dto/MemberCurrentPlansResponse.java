package com.project.fitness.domain.membership.dto;
import com.project.fitness.domain.fitness.model.DietPlan;
import com.project.fitness.domain.fitness.model.WorkoutPlan;
import com.project.fitness.domain.membership.model.Membership;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberCurrentPlansResponse {

  private PlanSummary workoutPlan;
  private PlanSummary dietPlan;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PlanSummary {
    private String id;
    private String title;
    private String description;
  }
}
