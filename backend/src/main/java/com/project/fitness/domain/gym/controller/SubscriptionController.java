package com.project.fitness.domain.gym.controller;
import com.project.fitness.domain.gym.dto.GymSubscribeRequest;
import com.project.fitness.domain.gym.dto.GymSubscriptionPlanRequest;
import com.project.fitness.domain.gym.dto.GymSubscriptionPlanResponse;
import com.project.fitness.domain.gym.dto.GymSubscriptionResponse;
import com.project.fitness.modules.gym.application.GymApplicationService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

  private final GymApplicationService gymApplicationService;

  @PostMapping("/plans")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<GymSubscriptionPlanResponse> createPlan(
      @Valid @RequestBody GymSubscriptionPlanRequest request) {
    return ResponseEntity.ok(gymApplicationService.createSubscriptionPlan(request));
  }

  @GetMapping("/plans")
  @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
  public ResponseEntity<List<GymSubscriptionPlanResponse>> getPlans(
      @RequestParam(defaultValue = "true") boolean activeOnly) {
    return ResponseEntity.ok(gymApplicationService.getSubscriptionPlans(activeOnly));
  }

  @PostMapping("/activate")
  @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
  public ResponseEntity<GymSubscriptionResponse> activate(
      Authentication authentication, @Valid @RequestBody GymSubscribeRequest request) {
    return ResponseEntity.ok(gymApplicationService.activateSubscription(
        (String) authentication.getPrincipal(), isAdmin(authentication), request));
  }

  @GetMapping("/gym/{gymId}")
  @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
  public ResponseEntity<GymSubscriptionResponse> getGymSubscription(
      Authentication authentication, @PathVariable String gymId) {
    return ResponseEntity.ok(gymApplicationService.getGymSubscription(
        (String) authentication.getPrincipal(), isAdmin(authentication), gymId));
  }

  private boolean isAdmin(Authentication authentication) {
    return authentication.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
  }
}
