package com.project.fitness.domain.fitness.controller;

import com.project.fitness.common.service.ExportService;
import com.project.fitness.domain.fitness.dto.ActivityRequest;
import com.project.fitness.domain.fitness.dto.ActivityResponse;
import com.project.fitness.domain.fitness.dto.ActivityStatisticsResponse;
import com.project.fitness.modules.fitness.application.FitnessApplicationService;
import com.project.fitness.domain.fitness.model.Activity;
import com.project.fitness.domain.fitness.repository.ActivityRepository;
import jakarta.validation.Valid;
import com.project.fitness.common.response.PagedResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/v1/activities")
public class ActivityController {
  private final FitnessApplicationService fitnessApplicationService;
  private final ExportService exportService;
  private final ActivityRepository activityRepository;

  public ActivityController(FitnessApplicationService fitnessApplicationService, ExportService exportService, ActivityRepository activityRepository) {
    this.fitnessApplicationService = fitnessApplicationService;
    this.exportService = exportService;
    this.activityRepository = activityRepository;
  }

  @PostMapping
  public ResponseEntity<ActivityResponse> trackActivity(
      @Valid @RequestBody ActivityRequest activityRequest,
      Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    activityRequest.setUserId(userId);
    return ResponseEntity.ok(fitnessApplicationService.trackActivity(activityRequest));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ActivityResponse> updateActivity(
      @PathVariable String id,
      @RequestBody com.project.fitness.domain.fitness.dto.ActivityUpdateRequest request,
      Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    return ResponseEntity.ok(fitnessApplicationService.updateActivity(id, userId, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteActivity(
      @PathVariable String id,
      Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    fitnessApplicationService.deleteActivity(id, userId);
    return ResponseEntity.noContent().build();
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

  @GetMapping("/statistics")
  public ResponseEntity<ActivityStatisticsResponse> getActivityStatistics(Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    return ResponseEntity.ok(fitnessApplicationService.getActivityStatistics(userId));
  }

  @GetMapping("/export")
  public ResponseEntity<byte[]> exportActivities(Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    java.util.List<Activity> activities = activityRepository.findByUser_Id(userId);
    byte[] csvData = exportService.exportActivitiesToCsv(activities);
    
    org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
    headers.set(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=activities.csv");
    headers.set(org.springframework.http.HttpHeaders.CONTENT_TYPE, "text/csv");
    
    return new ResponseEntity<>(csvData, headers, org.springframework.http.HttpStatus.OK);
  }

  @GetMapping("/export/all")
  @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<byte[]> exportAllActivities() {
    java.util.List<Activity> activities = activityRepository.findAll();
    byte[] csvData = exportService.exportActivitiesToCsv(activities);
    
    org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
    headers.set(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=all_activities.csv");
    headers.set(org.springframework.http.HttpHeaders.CONTENT_TYPE, "text/csv");
    
    return new ResponseEntity<>(csvData, headers, org.springframework.http.HttpStatus.OK);
  }
}
