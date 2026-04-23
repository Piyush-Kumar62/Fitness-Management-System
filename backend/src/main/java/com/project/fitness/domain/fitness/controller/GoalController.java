package com.project.fitness.domain.fitness.controller;
import com.project.fitness.domain.fitness.dto.GoalRequest;
import com.project.fitness.domain.fitness.dto.GoalResponse;
import com.project.fitness.domain.fitness.dto.MilestoneRequest;
import com.project.fitness.domain.fitness.dto.MilestoneResponse;
import com.project.fitness.modules.fitness.application.FitnessApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/goals")
public class GoalController {

  private final FitnessApplicationService fitnessApplicationService;

  public GoalController(FitnessApplicationService fitnessApplicationService) {
    this.fitnessApplicationService = fitnessApplicationService;
  }

  @PostMapping
  public ResponseEntity<GoalResponse> createGoal(
      @Valid @RequestBody GoalRequest request,
      Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    return ResponseEntity.ok(fitnessApplicationService.createGoal(request, userId));
  }

  @PutMapping("/{id}")
  public ResponseEntity<GoalResponse> updateGoal(
      @PathVariable String id,
      @Valid @RequestBody GoalRequest request,
      Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    return ResponseEntity.ok(fitnessApplicationService.updateGoal(id, request, userId));
  }

  @GetMapping("/{id}")
  public ResponseEntity<GoalResponse> getGoal(
      @PathVariable String id,
      Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    return ResponseEntity.ok(fitnessApplicationService.getGoalById(id, userId));
  }

  @GetMapping
  public ResponseEntity<List<GoalResponse>> getUserGoals(Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    return ResponseEntity.ok(fitnessApplicationService.getUserGoals(userId));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteGoal(
      @PathVariable String id,
      Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    fitnessApplicationService.deleteGoal(id, userId);
    return ResponseEntity.noContent().build();
  }

  // Milestone endpoints
  @PostMapping("/{goalId}/milestones")
  public ResponseEntity<MilestoneResponse> addMilestone(
      @PathVariable String goalId,
      @Valid @RequestBody MilestoneRequest request,
      Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    return ResponseEntity.ok(fitnessApplicationService.addMilestone(goalId, request, userId));
  }

  @PutMapping("/milestones/{milestoneId}/achieve")
  public ResponseEntity<MilestoneResponse> achieveMilestone(
      @PathVariable String milestoneId,
      Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    return ResponseEntity.ok(fitnessApplicationService.achieveMilestone(milestoneId, userId));
  }
}
