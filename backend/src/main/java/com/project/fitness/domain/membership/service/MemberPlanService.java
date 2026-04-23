package com.project.fitness.domain.membership.service;
import com.project.fitness.domain.membership.model.Membership;

import com.project.fitness.domain.membership.dto.MemberCurrentPlansResponse;
import com.project.fitness.domain.fitness.model.DietPlan;
import com.project.fitness.domain.membership.model.MemberPlan;
import com.project.fitness.domain.fitness.model.WorkoutPlan;
import com.project.fitness.domain.fitness.repository.DietPlanRepository;
import com.project.fitness.domain.membership.repository.MemberPlanRepository;
import com.project.fitness.domain.fitness.repository.WorkoutPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberPlanService {

  private final MemberPlanRepository memberPlanRepository;
  private final WorkoutPlanRepository workoutPlanRepository;
  private final DietPlanRepository dietPlanRepository;

  public MemberCurrentPlansResponse getCurrentPlans(String memberId) {
    MemberPlan assignment = memberPlanRepository
        .findFirstByMemberIdAndStatusOrderByAssignedAtDesc(memberId, MemberPlan.PlanStatus.ACTIVE)
        .orElse(null);
    if (assignment == null) {
      return MemberCurrentPlansResponse.builder().build();
    }
    return MemberCurrentPlansResponse.builder()
        .workoutPlan(resolveWorkoutSummary(assignment.getWorkoutPlanId()))
        .dietPlan(resolveDietSummary(assignment.getDietPlanId()))
        .build();
  }

  private MemberCurrentPlansResponse.PlanSummary resolveWorkoutSummary(String workoutPlanId) {
    if (workoutPlanId == null) {
      return null;
    }
    WorkoutPlan plan = workoutPlanRepository.findById(workoutPlanId).orElse(null);
    return plan == null ? null : MemberCurrentPlansResponse.PlanSummary.builder()
        .id(plan.getId())
        .title(plan.getTitle())
        .description(plan.getDescription())
        .build();
  }

  private MemberCurrentPlansResponse.PlanSummary resolveDietSummary(String dietPlanId) {
    if (dietPlanId == null) {
      return null;
    }
    DietPlan plan = dietPlanRepository.findById(dietPlanId).orElse(null);
    return plan == null ? null : MemberCurrentPlansResponse.PlanSummary.builder()
        .id(plan.getId())
        .title(plan.getTitle())
        .description(plan.getDescription())
        .build();
  }
}
