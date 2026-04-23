package com.project.fitness.domain.fitness.controller;

import com.project.fitness.domain.fitness.dto.ActivityRequest;
import com.project.fitness.domain.fitness.dto.ActivityResponse;
import com.project.fitness.modules.fitness.application.FitnessApplicationService;
import jakarta.validation.Valid;
import com.project.fitness.common.response.PagedResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/activities")
public class ActivityController {
  private final FitnessApplicationService fitnessApplicationService;

  public ActivityController(FitnessApplicationService fitnessApplicationService) {
    this.fitnessApplicationService = fitnessApplicationService;
  }

  @PostMapping
  public ResponseEntity<ActivityResponse> trackActivity(
      @Valid @RequestBody ActivityRequest activityRequest,
      Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    activityRequest.setUserId(userId);
    return ResponseEntity.ok(fitnessApplicationService.trackActivity(activityRequest));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ActivityResponse> getActivity(@PathVariable String id) {
    return ResponseEntity.ok(fitnessApplicationService.getActivityById(id));
  }

  @GetMapping
  public ResponseEntity<PagedResponse<ActivityResponse>> getUserActivities(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      Authentication authentication) {
    String userId = (authentication != null) ? (String) authentication.getPrincipal() : null;
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(fitnessApplicationService.getUserActivities(userId, pageable));
  }

  @GetMapping("/all")
  @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<PagedResponse<ActivityResponse>> getAllSystemActivities(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(fitnessApplicationService.getAllSystemActivities(pageable));
  }

  @GetMapping("/search")
  public ResponseEntity<PagedResponse<ActivityResponse>> searchActivities(
      @RequestParam(required = false) String type,
      @RequestParam(required = false) String dateFrom,
      @RequestParam(required = false) String dateTo,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(
        fitnessApplicationService.searchActivities(userId, type, dateFrom, dateTo, pageable));
  }
}
