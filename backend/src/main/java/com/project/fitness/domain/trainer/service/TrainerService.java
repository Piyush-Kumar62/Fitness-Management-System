package com.project.fitness.domain.trainer.service;
import com.project.fitness.domain.gym.model.Gym;
import com.project.fitness.domain.membership.model.Membership;

import com.project.fitness.domain.membership.dto.AssignPlanRequest;
import com.project.fitness.domain.fitness.dto.DietPlanRequest;
import com.project.fitness.domain.fitness.dto.DietPlanResponse;
import com.project.fitness.domain.membership.dto.MemberPlanResponse;
import com.project.fitness.domain.user.dto.UserResponse;
import com.project.fitness.domain.fitness.dto.WorkoutPlanRequest;
import com.project.fitness.domain.fitness.dto.WorkoutPlanResponse;
import com.project.fitness.common.exception.BadRequestException;
import com.project.fitness.common.exception.ResourceNotFoundException;
import com.project.fitness.common.exception.UnauthorizedException;
import com.project.fitness.mapper.DietPlanMapper;
import com.project.fitness.mapper.UserMapper;
import com.project.fitness.mapper.WorkoutPlanMapper;
import com.project.fitness.domain.fitness.model.DietMeal;
import com.project.fitness.domain.fitness.model.DietPlan;
import com.project.fitness.domain.membership.model.MemberPlan;
import com.project.fitness.domain.user.model.User;
import com.project.fitness.domain.user.model.UserRole;
import com.project.fitness.domain.fitness.model.WorkoutExercise;
import com.project.fitness.domain.fitness.model.WorkoutPlan;
import com.project.fitness.domain.fitness.repository.DietPlanRepository;
import com.project.fitness.domain.membership.repository.MemberPlanRepository;
import com.project.fitness.domain.user.repository.UserRepository;
import com.project.fitness.domain.fitness.repository.WorkoutPlanRepository;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Pageable;
import com.project.fitness.common.response.PagedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Implementation of {@link ITrainerService}. All entity↔DTO conversions delegate to MapStruct mappers.
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TrainerService implements ITrainerService {

  private final UserRepository userRepository;
  private final WorkoutPlanRepository workoutPlanRepository;
  private final DietPlanRepository dietPlanRepository;
  private final MemberPlanRepository memberPlanRepository;
  private final UserMapper userMapper;
  private final WorkoutPlanMapper workoutPlanMapper;
  private final DietPlanMapper dietPlanMapper;

  // ── Members ──────────────────────────────────────────────

  @Override
  @Transactional(readOnly = true)
  public PagedResponse<UserResponse> getAssignedMembers(String trainerId, Pageable pageable) {
    getTrainer(trainerId);
    return PagedResponse.from(userRepository.findByTrainerId(trainerId, pageable)
        .map(userMapper::toResponse));
  }

  @Override
  @Transactional(readOnly = true)
  public Map<String, Object> getMemberProgress(String trainerId, String memberId) {
    User trainer = getTrainer(trainerId);
    User member = getUser(memberId);
    assertAssignedToTrainer(trainerId, member);
    assertSameGym(trainer.getGymId(), member.getGymId());
    return Map.of(
        "member", userMapper.toResponse(member),
        "activePlans", memberPlanRepository.findByMemberIdAndStatus(memberId, MemberPlan.PlanStatus.ACTIVE),
        "totalActivities", member.getActivities().size()
    );
  }

  @Override
  @Transactional(readOnly = true)
  public Map<String, Object> getDashboardStats(String trainerId) {
    getTrainer(trainerId);
    return Map.of(
        "totalMembers", userRepository.findByTrainerId(trainerId).size(),
        "totalWorkoutPlans", workoutPlanRepository.countByTrainerId(trainerId),
        "totalDietPlans", dietPlanRepository.countByTrainerId(trainerId),
        "activePlanAssignments", memberPlanRepository.countByAssignedByAndStatus(trainerId, MemberPlan.PlanStatus.ACTIVE)
    );
  }

  // ── Workout Plans ────────────────────────────────────────

  @Override
  public WorkoutPlanResponse createWorkoutPlan(String trainerId, WorkoutPlanRequest request) {
    getTrainer(trainerId);
    WorkoutPlan plan = buildWorkoutPlan(trainerId, request);
    WorkoutPlanResponse response = workoutPlanMapper.toResponse(workoutPlanRepository.save(plan));
    return enrichWithTrainerName(response, trainerId);
  }

  @Override
  @Transactional(readOnly = true)
  public PagedResponse<WorkoutPlanResponse> getWorkoutPlans(String trainerId, Pageable pageable) {
    getTrainer(trainerId);
    return PagedResponse.from(workoutPlanRepository.findByTrainerId(trainerId, pageable)
        .map(p -> enrichWithTrainerName(workoutPlanMapper.toResponse(p), trainerId)));
  }

  @Override
  @Transactional(readOnly = true)
  public WorkoutPlanResponse getWorkoutPlanById(String trainerId, String planId) {
    getTrainer(trainerId);
    WorkoutPlan plan = getOwnedWorkoutPlan(trainerId, planId);
    return enrichWithTrainerName(workoutPlanMapper.toResponse(plan), trainerId);
  }

  @Override
  public WorkoutPlanResponse updateWorkoutPlan(String trainerId, String planId, WorkoutPlanRequest req) {
    getTrainer(trainerId);
    WorkoutPlan plan = getOwnedWorkoutPlan(trainerId, planId);
    applyWorkoutPlanUpdate(plan, req);
    WorkoutPlanResponse response = workoutPlanMapper.toResponse(workoutPlanRepository.save(plan));
    return enrichWithTrainerName(response, trainerId);
  }

  @Override
  public void deleteWorkoutPlan(String trainerId, String planId) {
    getTrainer(trainerId);
    workoutPlanRepository.delete(getOwnedWorkoutPlan(trainerId, planId));
  }

  // ── Diet Plans ───────────────────────────────────────────

  @Override
  public DietPlanResponse createDietPlan(String trainerId, DietPlanRequest request) {
    getTrainer(trainerId);
    DietPlan plan = buildDietPlan(trainerId, request);
    DietPlanResponse response = dietPlanMapper.toResponse(dietPlanRepository.save(plan));
    return enrichDietWithTrainerName(response, trainerId);
  }

  @Override
  @Transactional(readOnly = true)
  public PagedResponse<DietPlanResponse> getDietPlans(String trainerId, Pageable pageable) {
    getTrainer(trainerId);
    return PagedResponse.from(dietPlanRepository.findByTrainerId(trainerId, pageable)
        .map(p -> enrichDietWithTrainerName(dietPlanMapper.toResponse(p), trainerId)));
  }

  @Override
  @Transactional(readOnly = true)
  public DietPlanResponse getDietPlanById(String trainerId, String planId) {
    getTrainer(trainerId);
    DietPlan plan = getOwnedDietPlan(trainerId, planId);
    return enrichDietWithTrainerName(dietPlanMapper.toResponse(plan), trainerId);
  }

  @Override
  public DietPlanResponse updateDietPlan(String trainerId, String planId, DietPlanRequest req) {
    getTrainer(trainerId);
    DietPlan plan = getOwnedDietPlan(trainerId, planId);
    applyDietPlanUpdate(plan, req);
    DietPlanResponse response = dietPlanMapper.toResponse(dietPlanRepository.save(plan));
    return enrichDietWithTrainerName(response, trainerId);
  }

  @Override
  public void deleteDietPlan(String trainerId, String planId) {
    getTrainer(trainerId);
    dietPlanRepository.delete(getOwnedDietPlan(trainerId, planId));
  }

  // ── Plan Assignment ──────────────────────────────────────

  @Override
  public MemberPlanResponse assignPlanToMember(String trainerId, AssignPlanRequest request) {
    User trainer = getTrainer(trainerId);
    User member = getUser(request.getMemberId());
    validateAssignment(trainer, member, trainerId, request);
    MemberPlan saved = memberPlanRepository.save(MemberPlan.builder()
        .memberId(request.getMemberId())
        .workoutPlanId(request.getWorkoutPlanId())
        .dietPlanId(request.getDietPlanId())
        .assignedBy(trainerId)
        .build());
    return buildMemberPlanResponse(saved);
  }

  // ── Private Helpers ──────────────────────────────────────

  private User getTrainer(String trainerId) {
    User trainer = getUser(trainerId);
    if (trainer.getRole() != UserRole.TRAINER) {
      throw new UnauthorizedException("Only TRAINER users can perform this action");
    }
    return trainer;
  }

  private User getUser(String userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
  }

  private void assertSameGym(String gymA, String gymB) {
    if (gymA == null || !gymA.equals(gymB)) {
      throw new UnauthorizedException("Cross-gym operation is not allowed");
    }
  }

  private void assertAssignedToTrainer(String trainerId, User member) {
    if (!trainerId.equals(member.getTrainerId())) {
      throw new UnauthorizedException("This member is not assigned to you");
    }
  }

  private WorkoutPlan getOwnedWorkoutPlan(String trainerId, String planId) {
    WorkoutPlan plan = workoutPlanRepository.findById(planId)
        .orElseThrow(() -> new ResourceNotFoundException("WorkoutPlan", "id", planId));
    if (!plan.getTrainerId().equals(trainerId)) {
      throw new UnauthorizedException("You don't own this workout plan");
    }
    return plan;
  }

  private DietPlan getOwnedDietPlan(String trainerId, String planId) {
    DietPlan plan = dietPlanRepository.findById(planId)
        .orElseThrow(() -> new ResourceNotFoundException("DietPlan", "id", planId));
    if (!plan.getTrainerId().equals(trainerId)) {
      throw new UnauthorizedException("You don't own this diet plan");
    }
    return plan;
  }

  private WorkoutPlan buildWorkoutPlan(String trainerId, WorkoutPlanRequest req) {
    WorkoutPlan plan = WorkoutPlan.builder()
        .trainerId(trainerId).title(req.getTitle())
        .description(req.getDescription())
        .difficulty(req.getDifficulty() != null ? req.getDifficulty() : WorkoutPlan.Difficulty.BEGINNER)
        .durationWeeks(req.getDurationWeeks()).build();
    if (req.getExercises() != null) {
      req.getExercises().forEach(ex -> plan.getExercises().add(buildExercise(plan, ex)));
    }
    return plan;
  }

  private WorkoutExercise buildExercise(WorkoutPlan plan, WorkoutPlanRequest.ExerciseRequest ex) {
    return WorkoutExercise.builder().workoutPlan(plan).name(ex.getName())
        .sets(ex.getSets()).reps(ex.getReps()).durationMinutes(ex.getDurationMinutes())
        .day(ex.getDay()).restSeconds(ex.getRestSeconds()).notes(ex.getNotes()).build();
  }

  private void applyWorkoutPlanUpdate(WorkoutPlan plan, WorkoutPlanRequest req) {
    plan.setTitle(req.getTitle());
    plan.setDescription(req.getDescription());
    if (req.getDifficulty() != null) plan.setDifficulty(req.getDifficulty());
    plan.setDurationWeeks(req.getDurationWeeks());
    if (req.getExercises() != null) {
      plan.getExercises().clear();
      req.getExercises().forEach(ex -> plan.getExercises().add(buildExercise(plan, ex)));
    }
  }

  private DietPlan buildDietPlan(String trainerId, DietPlanRequest req) {
    DietPlan plan = DietPlan.builder().trainerId(trainerId).title(req.getTitle())
        .description(req.getDescription()).targetCalories(req.getTargetCalories())
        .targetProtein(req.getTargetProtein()).targetCarbs(req.getTargetCarbs())
        .targetFat(req.getTargetFat()).build();
    if (req.getMeals() != null) {
      req.getMeals().forEach(m -> plan.getMeals().add(buildMeal(plan, m)));
    }
    return plan;
  }

  private DietMeal buildMeal(DietPlan plan, DietPlanRequest.MealRequest m) {
    return DietMeal.builder().dietPlan(plan).mealType(m.getMealType())
        .name(m.getName()).calories(m.getCalories()).description(m.getDescription()).build();
  }

  private void applyDietPlanUpdate(DietPlan plan, DietPlanRequest req) {
    plan.setTitle(req.getTitle()); plan.setDescription(req.getDescription());
    plan.setTargetCalories(req.getTargetCalories()); plan.setTargetProtein(req.getTargetProtein());
    plan.setTargetCarbs(req.getTargetCarbs()); plan.setTargetFat(req.getTargetFat());
    if (req.getMeals() != null) {
      plan.getMeals().clear();
      req.getMeals().forEach(m -> plan.getMeals().add(buildMeal(plan, m)));
    }
  }

  private void validateAssignment(User trainer, User member, String trainerId, AssignPlanRequest req) {
    if (member.getRole() != UserRole.MEMBER) throw new BadRequestException("Can only assign plans to members");
    assertAssignedToTrainer(trainerId, member);
    assertSameGym(trainer.getGymId(), member.getGymId());
    if (req.getWorkoutPlanId() == null && req.getDietPlanId() == null) {
      throw new BadRequestException("At least one plan must be specified");
    }
    if (req.getWorkoutPlanId() != null) getOwnedWorkoutPlan(trainerId, req.getWorkoutPlanId());
    if (req.getDietPlanId() != null) getOwnedDietPlan(trainerId, req.getDietPlanId());
  }

  private WorkoutPlanResponse enrichWithTrainerName(WorkoutPlanResponse r, String trainerId) {
    String name = resolveUserName(trainerId);
    return WorkoutPlanResponse.builder().id(r.getId()).trainerId(r.getTrainerId())
        .trainerName(name).title(r.getTitle()).description(r.getDescription())
        .difficulty(r.getDifficulty()).durationWeeks(r.getDurationWeeks())
        .exerciseCount(r.getExerciseCount()).createdAt(r.getCreatedAt())
        .updatedAt(r.getUpdatedAt()).exercises(r.getExercises()).build();
  }

  private DietPlanResponse enrichDietWithTrainerName(DietPlanResponse r, String trainerId) {
    String name = resolveUserName(trainerId);
    return DietPlanResponse.builder().id(r.getId()).trainerId(r.getTrainerId())
        .trainerName(name).title(r.getTitle()).description(r.getDescription())
        .targetCalories(r.getTargetCalories()).targetProtein(r.getTargetProtein())
        .targetCarbs(r.getTargetCarbs()).targetFat(r.getTargetFat())
        .mealCount(r.getMealCount()).createdAt(r.getCreatedAt())
        .updatedAt(r.getUpdatedAt()).meals(r.getMeals()).build();
  }

  private String resolveUserName(String userId) {
    return userRepository.findById(userId)
        .map(u -> u.getFirstName() + " " + u.getLastName()).orElse("Unknown");
  }

  private MemberPlanResponse buildMemberPlanResponse(MemberPlan mp) {
    return MemberPlanResponse.builder().id(mp.getId()).memberId(mp.getMemberId())
        .memberName(resolveUserName(mp.getMemberId()))
        .workoutPlanId(mp.getWorkoutPlanId())
        .workoutPlanTitle(mp.getWorkoutPlanId() != null
            ? workoutPlanRepository.findById(mp.getWorkoutPlanId()).map(WorkoutPlan::getTitle).orElse("Deleted Plan") : null)
        .dietPlanId(mp.getDietPlanId())
        .dietPlanTitle(mp.getDietPlanId() != null
            ? dietPlanRepository.findById(mp.getDietPlanId()).map(DietPlan::getTitle).orElse("Deleted Plan") : null)
        .assignedBy(mp.getAssignedBy()).assignedByName(resolveUserName(mp.getAssignedBy()))
        .status(mp.getStatus()).assignedAt(mp.getAssignedAt()).build();
  }
}
