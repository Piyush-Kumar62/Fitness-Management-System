package com.project.fitness.domain.owner.controller;

import com.project.fitness.common.response.PagedResponse;
import com.project.fitness.domain.user.dto.UserResponse;
import com.project.fitness.modules.owner.application.OwnerApplicationService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/owner")
@RequiredArgsConstructor
@PreAuthorize("hasRole('OWNER')")
public class OwnerController {

  private final OwnerApplicationService ownerApplicationService;

  @GetMapping("/trainers")
  public ResponseEntity<PagedResponse<UserResponse>> getOwnerTrainers(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      Authentication authentication) {
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(
        ownerApplicationService.getOwnerTrainers((String) authentication.getPrincipal(), pageable));
  }

  @GetMapping("/members")
  public ResponseEntity<PagedResponse<UserResponse>> getOwnerMembers(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      Authentication authentication) {
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(
        ownerApplicationService.getOwnerMembers((String) authentication.getPrincipal(), pageable));
  }

  @GetMapping("/revenue")
  public ResponseEntity<Map<String, Object>> getOwnerRevenue(Authentication authentication) {
    return ResponseEntity.ok(
        ownerApplicationService.getOwnerRevenueSummary((String) authentication.getPrincipal()));
  }

  @PostMapping("/trainers")
  public ResponseEntity<UserResponse> createTrainer(
      Authentication authentication,
      @jakarta.validation.Valid @org.springframework.web.bind.annotation.RequestBody com.project.fitness.domain.user.dto.CreateTrainerRequest request) {
    return ResponseEntity.ok(
        ownerApplicationService.createTrainer((String) authentication.getPrincipal(), request));
  }

  @PostMapping("/trainers/{trainerId}/assign-gym/{gymId}")
  public ResponseEntity<UserResponse> assignTrainerToGym(
      Authentication authentication,
      @org.springframework.web.bind.annotation.PathVariable String trainerId,
      @org.springframework.web.bind.annotation.PathVariable String gymId) {
    return ResponseEntity.ok(
        ownerApplicationService.assignTrainerToGym((String) authentication.getPrincipal(), trainerId, gymId));
  }

  @org.springframework.web.bind.annotation.DeleteMapping("/trainers/{trainerId}")
  public ResponseEntity<Void> removeTrainer(
      Authentication authentication,
      @org.springframework.web.bind.annotation.PathVariable String trainerId) {
    ownerApplicationService.removeTrainer((String) authentication.getPrincipal(), trainerId);
    return ResponseEntity.noContent().build();
  }
}

