package com.project.fitness.domain.membership.controller;
import com.project.fitness.domain.membership.dto.MemberCurrentPlansResponse;
import com.project.fitness.modules.membership.application.MembershipApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/member/plans")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MEMBER')")
public class MemberPlanController {

  private final MembershipApplicationService membershipApplicationService;

  @GetMapping("/current")
  public ResponseEntity<MemberCurrentPlansResponse> getCurrentPlans(Authentication authentication) {
    return ResponseEntity.ok(
        membershipApplicationService.getCurrentPlans((String) authentication.getPrincipal()));
  }
}
