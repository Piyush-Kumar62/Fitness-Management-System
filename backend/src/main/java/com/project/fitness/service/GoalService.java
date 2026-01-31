package com.project.fitness.service;

import com.project.fitness.dto.GoalRequest;
import com.project.fitness.dto.GoalResponse;
import com.project.fitness.dto.MilestoneRequest;
import com.project.fitness.dto.MilestoneResponse;
import com.project.fitness.exceptions.BadRequestException;
import com.project.fitness.exceptions.ResourceNotFoundException;
import com.project.fitness.model.Goal;
import com.project.fitness.model.Milestone;
import com.project.fitness.model.User;
import com.project.fitness.repository.GoalRepository;
import com.project.fitness.repository.MilestoneRepository;
import com.project.fitness.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoalService {

  private final GoalRepository goalRepository;
  private final MilestoneRepository milestoneRepository;
  private final UserRepository userRepository;

  public GoalService(GoalRepository goalRepository,
                     MilestoneRepository milestoneRepository,
                     UserRepository userRepository) {
    this.goalRepository = goalRepository;
    this.milestoneRepository = milestoneRepository;
    this.userRepository = userRepository;
  }

  public GoalResponse createGoal(GoalRequest request, String userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Goal goal = Goal.builder()
        .user(user)
        .title(request.getTitle())
        .description(request.getDescription())
        .type(request.getType())
        .targetValue(request.getTargetValue())
        .currentValue(request.getCurrentValue() != null ? request.getCurrentValue() : 0.0)
        .unit(request.getUnit())
        .startDate(request.getStartDate())
        .deadline(request.getDeadline())
        .build();
    
    // Status defaults to ACTIVE via @Builder.Default, don't override unless explicitly provided
    if (request.getStatus() != null) {
      goal.setStatus(request.getStatus());
    }

    Goal saved = goalRepository.save(goal);
    return mapToResponse(saved);
  }

  public GoalResponse updateGoal(String goalId, GoalRequest request, String userId) {
    Goal goal = goalRepository.findById(goalId)
        .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

    if (!goal.getUser().getId().equals(userId)) {
      throw new BadRequestException("Unauthorized to update this goal");
    }

    goal.setTitle(request.getTitle());
    goal.setDescription(request.getDescription());
    goal.setType(request.getType());
    goal.setTargetValue(request.getTargetValue());
    goal.setCurrentValue(request.getCurrentValue());
    goal.setUnit(request.getUnit());
    goal.setStartDate(request.getStartDate());
    goal.setDeadline(request.getDeadline());
    if (request.getStatus() != null) {
      goal.setStatus(request.getStatus());
    }


    Goal updated = goalRepository.save(goal);
    return mapToResponse(updated);
  }

  public GoalResponse getGoalById(String goalId, String userId) {
    Goal goal = goalRepository.findById(goalId)
        .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

    if (!goal.getUser().getId().equals(userId)) {
      throw new BadRequestException("Unauthorized to view this goal");
    }

    return mapToResponse(goal);
  }

  public List<GoalResponse> getUserGoals(String userId) {
    return goalRepository.findByUser_IdOrderByCreatedAtDesc(userId).stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
  }

  public void deleteGoal(String goalId, String userId) {
    Goal goal = goalRepository.findById(goalId)
        .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

    if (!goal.getUser().getId().equals(userId)) {
      throw new BadRequestException("Unauthorized to delete this goal");
    }

    goalRepository.delete(goal);
  }

  // Milestone operations
  public MilestoneResponse addMilestone(String goalId, MilestoneRequest request, String userId) {
    Goal goal = goalRepository.findById(goalId)
        .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

    if (!goal.getUser().getId().equals(userId)) {
      throw new BadRequestException("Unauthorized");
    }

    Milestone milestone = Milestone.builder()
        .goal(goal)
        .title(request.getTitle())
        .description(request.getDescription())
        .targetValue(request.getTargetValue())
        .achieved(false)
        .build();

    Milestone saved = milestoneRepository.save(milestone);
    return mapMilestoneToResponse(saved);
  }

  public MilestoneResponse achieveMilestone(String milestoneId, String userId) {
    Milestone milestone = milestoneRepository.findById(milestoneId)
        .orElseThrow(() -> new ResourceNotFoundException("Milestone not found"));

    if (!milestone.getGoal().getUser().getId().equals(userId)) {
      throw new BadRequestException("Unauthorized");
    }

    milestone.setAchieved(true);
    milestone.setAchievedAt(LocalDateTime.now());

    Milestone updated = milestoneRepository.save(milestone);
    return mapMilestoneToResponse(updated);
  }

  private GoalResponse mapToResponse(Goal goal) {
    // Calculate progress percentage
    Double progress = 0.0;
    if (goal.getTargetValue() != null && goal.getTargetValue() > 0) {
      progress = (goal.getCurrentValue() / goal.getTargetValue()) * 100;
      progress = Math.min(progress, 100.0);
    }

    // Get milestones
    List<MilestoneResponse> milestones = milestoneRepository.findByGoal_IdOrderByTargetValueAsc(goal.getId())
        .stream()
        .map(this::mapMilestoneToResponse)
        .collect(Collectors.toList());

    return new GoalResponse(
        goal.getId(),
        goal.getUser().getId(),
        goal.getTitle(),
        goal.getDescription(),
        goal.getType(),
        goal.getTargetValue(),
        goal.getCurrentValue(),
        goal.getUnit(),
        goal.getStartDate(),
        goal.getDeadline(),
        goal.getStatus(),
        progress,
        milestones,
        goal.getCreatedAt(),
        goal.getUpdatedAt()
    );
  }

  private MilestoneResponse mapMilestoneToResponse(Milestone milestone) {
    return new MilestoneResponse(
        milestone.getId(),
        milestone.getGoal().getId(),
        milestone.getTitle(),
        milestone.getDescription(),
        milestone.getTargetValue(),
        milestone.getAchieved(),
        milestone.getAchievedAt(),
        milestone.getCreatedAt()
    );
  }
}
