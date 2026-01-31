package com.project.fitness.controller;

import com.project.fitness.dto.GoalRequest;
import com.project.fitness.dto.GoalResponse;
import com.project.fitness.dto.MilestoneRequest;
import com.project.fitness.dto.MilestoneResponse;
import com.project.fitness.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

  private final GoalService goalService;

  public GoalController(GoalService goalService) {
    this.goalService = goalService;
  }

  @PostMapping
  public ResponseEntity<GoalResponse> createGoal(
      @Valid @RequestBody GoalRequest request,
      Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    return ResponseEntity.ok(goalService.createGoal(request, userId));
  }

  @PutMapping("/{id}")
  public ResponseEntity<GoalResponse> updateGoal(
      @PathVariable String id,
      @Valid @RequestBody GoalRequest request,
      Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    return ResponseEntity.ok(goalService.updateGoal(id, request, userId));
  }

  @GetMapping("/{id}")
  public ResponseEntity<GoalResponse> getGoal(
      @PathVariable String id,
      Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    return ResponseEntity.ok(goalService.getGoalById(id, userId));
  }

  @GetMapping
  public ResponseEntity<List<GoalResponse>> getUserGoals(Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    return ResponseEntity.ok(goalService.getUserGoals(userId));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteGoal(
      @PathVariable String id,
      Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    goalService.deleteGoal(id, userId);
    return ResponseEntity.noContent().build();
  }

  // Milestone endpoints
  @PostMapping("/{goalId}/milestones")
  public ResponseEntity<MilestoneResponse> addMilestone(
      @PathVariable String goalId,
      @Valid @RequestBody MilestoneRequest request,
      Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    return ResponseEntity.ok(goalService.addMilestone(goalId, request, userId));
  }

  @PutMapping("/milestones/{milestoneId}/achieve")
  public ResponseEntity<MilestoneResponse> achieveMilestone(
      @PathVariable String milestoneId,
      Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    return ResponseEntity.ok(goalService.achieveMilestone(milestoneId, userId));
  }
}
