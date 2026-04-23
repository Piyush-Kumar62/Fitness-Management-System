package com.project.fitness.domain.user.controller;
import com.project.fitness.modules.user.application.UserApplicationService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.cache.annotation.Cacheable;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

  private final UserApplicationService userApplicationService;

  @GetMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  @Cacheable(value = "dashboardAdminStats", key = "'admin_stats'")
  public ResponseEntity<Map<String, Object>> getAdminStats() {
    return ResponseEntity.ok(userApplicationService.getAdminDashboardStats());
  }

  @GetMapping("/owner")
  @PreAuthorize("hasRole('OWNER')")
  @Cacheable(value = "dashboardOwnerStats", key = "#authentication.principal")
  public ResponseEntity<Map<String, Object>> getOwnerStats(Authentication authentication) {
    return ResponseEntity.ok(
        userApplicationService.getOwnerDashboardStats((String) authentication.getPrincipal()));
  }

  @GetMapping("/trainer")
  @PreAuthorize("hasRole('TRAINER')")
  @Cacheable(value = "dashboardTrainerStats", key = "#authentication.principal")
  public ResponseEntity<Map<String, Object>> getTrainerStats(Authentication authentication) {
    return ResponseEntity.ok(
        userApplicationService.getTrainerDashboardStats((String) authentication.getPrincipal()));
  }

  @GetMapping("/member")
  @PreAuthorize("hasRole('MEMBER')")
  @Cacheable(value = "dashboardMemberStats", key = "#authentication.principal")
  public ResponseEntity<Map<String, Object>> getMemberStats(Authentication authentication) {
    return ResponseEntity.ok(
        userApplicationService.getMemberDashboardStats((String) authentication.getPrincipal()));
  }
}
