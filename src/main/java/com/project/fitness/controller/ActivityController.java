package com.project.fitness.controller;

import com.project.fitness.dto.ActivityRequest;
import com.project.fitness.dto.ActivityResponse;
import com.project.fitness.service.ActivityService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {
  private final ActivityService activityService;

  public ActivityController(ActivityService activityService) {
    this.activityService = activityService;
  }

  @PostMapping
  public ResponseEntity<ActivityResponse> trackActivity(@RequestBody ActivityRequest activityRequest) {
    return ResponseEntity.ok(activityService.trackActivity(activityRequest));
  }

  @GetMapping("/{id}") // Matches GET /api/activities/{id} in Postman
  public ResponseEntity<ActivityResponse> getActivity(@PathVariable String id) {
    return ResponseEntity.ok(activityService.getActivityById(id));
  }

  @GetMapping
  public ResponseEntity<List<ActivityResponse>> getUserActivities(Authentication authentication) {
    String userId = (authentication != null) ? (String) authentication.getPrincipal() : null;
    return ResponseEntity.ok(activityService.getUserActivities(userId));
  }
}