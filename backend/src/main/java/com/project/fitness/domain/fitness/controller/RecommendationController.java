package com.project.fitness.domain.fitness.controller;
import com.project.fitness.domain.fitness.dto.RecommendationRequest;
import com.project.fitness.domain.fitness.dto.RecommendationResponse;
import com.project.fitness.modules.fitness.application.FitnessApplicationService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/recommendations")
public class RecommendationController {
  private final FitnessApplicationService fitnessApplicationService;

  public RecommendationController(FitnessApplicationService fitnessApplicationService) {
    this.fitnessApplicationService = fitnessApplicationService;
  }

  @PostMapping("/generate")
  public ResponseEntity<RecommendationResponse> generate(@Valid @RequestBody RecommendationRequest request) {
    return ResponseEntity.ok(fitnessApplicationService.generateRecommendation(request));
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<RecommendationResponse>> getByUser(@PathVariable String userId) {
    return ResponseEntity.ok(fitnessApplicationService.getUserRecommendations(userId));
  }

  @GetMapping("/activity/{activityId}")
  public ResponseEntity<List<RecommendationResponse>> getByActivity(@PathVariable String activityId) {
    return ResponseEntity.ok(fitnessApplicationService.getActivityRecommendations(activityId));
  }

  @GetMapping("/{id}")
  public ResponseEntity<RecommendationResponse> getById(@PathVariable String id) {
    return ResponseEntity.ok(fitnessApplicationService.getRecommendationById(id));
  }

  @PostMapping
  public ResponseEntity<RecommendationResponse> create(@Valid @RequestBody RecommendationRequest request) {
    return ResponseEntity.ok(fitnessApplicationService.createRecommendation(request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    fitnessApplicationService.deleteRecommendation(id);
    return ResponseEntity.noContent().build();
  }
}
