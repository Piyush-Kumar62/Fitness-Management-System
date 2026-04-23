package com.project.fitness.domain.trainer.service;
import com.project.fitness.domain.user.model.User;
import com.project.fitness.domain.membership.model.Membership;

import com.project.fitness.domain.fitness.dto.DietPlanRequest;
import com.project.fitness.domain.fitness.dto.DietPlanResponse;
import com.project.fitness.domain.membership.dto.MemberPlanResponse;
import com.project.fitness.domain.membership.dto.AssignPlanRequest;
import com.project.fitness.domain.user.dto.UserResponse;
import com.project.fitness.domain.fitness.dto.WorkoutPlanRequest;
import com.project.fitness.domain.fitness.dto.WorkoutPlanResponse;
import org.springframework.data.domain.Pageable;
import com.project.fitness.common.response.PagedResponse;
import java.util.List;
import java.util.Map;

// Contract for all trainer operations. Controllers depend on this interface, not the concrete implementation.
public interface ITrainerService {

  // Member management
  PagedResponse<UserResponse> getAssignedMembers(String trainerId, Pageable pageable);
  Map<String, Object> getMemberProgress(String trainerId, String memberId);
  Map<String, Object> getDashboardStats(String trainerId);

  // Workout plans
  WorkoutPlanResponse createWorkoutPlan(String trainerId, WorkoutPlanRequest request);
  PagedResponse<WorkoutPlanResponse> getWorkoutPlans(String trainerId, Pageable pageable);
  WorkoutPlanResponse getWorkoutPlanById(String trainerId, String planId);
  WorkoutPlanResponse updateWorkoutPlan(String trainerId, String planId, WorkoutPlanRequest req);
  void deleteWorkoutPlan(String trainerId, String planId);

  // Diet plans
  DietPlanResponse createDietPlan(String trainerId, DietPlanRequest request);
  PagedResponse<DietPlanResponse> getDietPlans(String trainerId, Pageable pageable);
  DietPlanResponse getDietPlanById(String trainerId, String planId);
  DietPlanResponse updateDietPlan(String trainerId, String planId, DietPlanRequest request);
  void deleteDietPlan(String trainerId, String planId);

  // Plan assignment
  MemberPlanResponse assignPlanToMember(String trainerId, AssignPlanRequest request);
}
