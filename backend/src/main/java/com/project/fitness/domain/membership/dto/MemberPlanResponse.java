package com.project.fitness.domain.membership.dto;
import com.project.fitness.domain.membership.model.MemberPlan;
import com.project.fitness.domain.membership.model.Membership;

import com.project.fitness.domain.membership.model.MemberPlan.PlanStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberPlanResponse {

  private String id;
  private String memberId;
  private String memberName;
  private String workoutPlanId;
  private String workoutPlanTitle;
  private String dietPlanId;
  private String dietPlanTitle;
  private String assignedBy;
  private String assignedByName;
  private PlanStatus status;
  private LocalDateTime assignedAt;
}
