package com.project.fitness.controller;

import com.project.fitness.dto.RecommendationRequest;
import com.project.fitness.dto.RecommendationResponse;
import com.project.fitness.service.RecommendationService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {
  private final RecommendationService recommendationService;

  public RecommendationController(RecommendationService recommendationService) {
    this.recommendationService = recommendationService;
  }

  @PostMapping("/generate")
  public ResponseEntity<RecommendationResponse> generate(@Valid @RequestBody RecommendationRequest request) {
    return ResponseEntity.ok(recommendationService.generateRecommendation(request));
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<RecommendationResponse>> getByUser(@PathVariable String userId) {
    return ResponseEntity.ok(recommendationService.getUserRecommendations(userId));
  }

  @GetMapping("/activity/{activityId}")
  public ResponseEntity<List<RecommendationResponse>> getByActivity(@PathVariable String activityId) {
    return ResponseEntity.ok(recommendationService.getActivityRecommendations(activityId));
  }

  @GetMapping("/{id}")
  public ResponseEntity<RecommendationResponse> getById(@PathVariable String id) {
    return ResponseEntity.ok(recommendationService.getRecommendationById(id));
  }

  @PostMapping
  public ResponseEntity<RecommendationResponse> create(@Valid @RequestBody RecommendationRequest request) {
    return ResponseEntity.ok(recommendationService.createRecommendation(request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    recommendationService.deleteRecommendation(id);
    return ResponseEntity.noContent().build();
  }
}