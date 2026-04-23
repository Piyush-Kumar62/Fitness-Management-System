package com.project.fitness.domain.trainer.controller;
import com.project.fitness.domain.user.model.User;
import com.project.fitness.domain.membership.model.Membership;

import com.project.fitness.domain.membership.dto.AssignPlanRequest;
import com.project.fitness.domain.fitness.dto.DietPlanRequest;
import com.project.fitness.domain.fitness.dto.DietPlanResponse;
import com.project.fitness.domain.membership.dto.MemberPlanResponse;
import com.project.fitness.domain.user.dto.UserResponse;
import com.project.fitness.domain.fitness.dto.WorkoutPlanRequest;
import com.project.fitness.domain.fitness.dto.WorkoutPlanResponse;
import com.project.fitness.domain.trainer.service.TrainerService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.project.fitness.common.response.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trainer")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TRAINER')")
public class TrainerController {

  private final TrainerService trainerService;

  @GetMapping("/members")
  public ResponseEntity<PagedResponse<UserResponse>> getAssignedMembers(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      Authentication authentication) {
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(trainerService.getAssignedMembers((String) authentication.getPrincipal(), pageable));
  }

  @GetMapping("/members/{memberId}/progress")
  public ResponseEntity<Map<String, Object>> getMemberProgress(
      Authentication authentication, @PathVariable String memberId) {
    return ResponseEntity.ok(trainerService.getMemberProgress((String) authentication.getPrincipal(), memberId));
  }

  @GetMapping("/dashboard/stats")
  public ResponseEntity<Map<String, Object>> getDashboardStats(Authentication authentication) {
    return ResponseEntity.ok(trainerService.getDashboardStats((String) authentication.getPrincipal()));
  }

  @PostMapping("/workout-plans")
  public ResponseEntity<WorkoutPlanResponse> createWorkoutPlan(
      Authentication authentication, @Valid @RequestBody WorkoutPlanRequest request) {
    return ResponseEntity.ok(trainerService.createWorkoutPlan((String) authentication.getPrincipal(), request));
  }

  @GetMapping("/workout-plans")
  public ResponseEntity<PagedResponse<WorkoutPlanResponse>> getWorkoutPlans(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      Authentication authentication) {
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(trainerService.getWorkoutPlans((String) authentication.getPrincipal(), pageable));
  }

  @GetMapping("/workout-plans/{planId}")
  public ResponseEntity<WorkoutPlanResponse> getWorkoutPlanById(
      Authentication authentication, @PathVariable String planId) {
    return ResponseEntity.ok(trainerService.getWorkoutPlanById((String) authentication.getPrincipal(), planId));
  }

  @PutMapping("/workout-plans/{planId}")
  public ResponseEntity<WorkoutPlanResponse> updateWorkoutPlan(
      Authentication authentication, @PathVariable String planId, @Valid @RequestBody WorkoutPlanRequest request) {
    return ResponseEntity.ok(trainerService.updateWorkoutPlan((String) authentication.getPrincipal(), planId, request));
  }

  @DeleteMapping("/workout-plans/{planId}")
  public ResponseEntity<Void> deleteWorkoutPlan(Authentication authentication, @PathVariable String planId) {
    trainerService.deleteWorkoutPlan((String) authentication.getPrincipal(), planId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/diet-plans")
  public ResponseEntity<DietPlanResponse> createDietPlan(
      Authentication authentication, @Valid @RequestBody DietPlanRequest request) {
    return ResponseEntity.ok(trainerService.createDietPlan((String) authentication.getPrincipal(), request));
  }

  @GetMapping("/diet-plans")
  public ResponseEntity<PagedResponse<DietPlanResponse>> getDietPlans(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      Authentication authentication) {
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(trainerService.getDietPlans((String) authentication.getPrincipal(), pageable));
  }

  @GetMapping("/diet-plans/{planId}")
  public ResponseEntity<DietPlanResponse> getDietPlanById(
      Authentication authentication, @PathVariable String planId) {
    return ResponseEntity.ok(trainerService.getDietPlanById((String) authentication.getPrincipal(), planId));
  }

  @PutMapping("/diet-plans/{planId}")
  public ResponseEntity<DietPlanResponse> updateDietPlan(
      Authentication authentication, @PathVariable String planId, @Valid @RequestBody DietPlanRequest request) {
    return ResponseEntity.ok(trainerService.updateDietPlan((String) authentication.getPrincipal(), planId, request));
  }

  @DeleteMapping("/diet-plans/{planId}")
  public ResponseEntity<Void> deleteDietPlan(Authentication authentication, @PathVariable String planId) {
    trainerService.deleteDietPlan((String) authentication.getPrincipal(), planId);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/member-plans/assign")
  public ResponseEntity<MemberPlanResponse> assignPlan(
      Authentication authentication, @Valid @RequestBody AssignPlanRequest request) {
    return ResponseEntity.ok(trainerService.assignPlanToMember((String) authentication.getPrincipal(), request));
  }
}
