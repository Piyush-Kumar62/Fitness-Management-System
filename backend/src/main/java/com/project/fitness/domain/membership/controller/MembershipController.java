package com.project.fitness.domain.membership.controller;
import com.project.fitness.domain.membership.dto.BuyMembershipRequest;
import com.project.fitness.domain.membership.dto.MembershipPlanRequest;
import com.project.fitness.domain.membership.dto.MembershipPlanResponse;
import com.project.fitness.domain.membership.dto.MembershipPurchaseResponse;
import com.project.fitness.domain.membership.dto.MembershipResponse;
import com.project.fitness.domain.payment.dto.PaymentResponse;
import com.project.fitness.modules.membership.application.MembershipApplicationService;
import com.project.fitness.common.response.PagedResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api", "/api/v1"})
@RequiredArgsConstructor
public class MembershipController {

  private final MembershipApplicationService membershipApplicationService;

  @PostMapping("/membership-plans")
  @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
  public ResponseEntity<MembershipPlanResponse> createPlan(
      Authentication authentication,
      @Valid @RequestBody MembershipPlanRequest request) {
    return ResponseEntity.ok(membershipApplicationService.createPlan(
        (String) authentication.getPrincipal(), isAdmin(authentication), request));
  }

  @GetMapping("/membership-plans")
  @PreAuthorize("hasAnyRole('MEMBER','TRAINER','OWNER','ADMIN')")
  public ResponseEntity<List<MembershipPlanResponse>> getPlans(
      @RequestParam String gymId,
      @RequestParam(defaultValue = "true") boolean activeOnly) {
    return ResponseEntity.ok(membershipApplicationService.getPlans(gymId, activeOnly));
  }

  @PostMapping("/memberships/buy")
  @PreAuthorize("hasAnyRole('MEMBER','OWNER','ADMIN')")
  public ResponseEntity<MembershipPurchaseResponse> buyMembership(
      Authentication authentication,
      @Valid @RequestBody BuyMembershipRequest request) {
    return ResponseEntity.ok(membershipApplicationService.buyMembership(
        (String) authentication.getPrincipal(), isMember(authentication), request));
  }

  @GetMapping("/memberships/history")
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<PagedResponse<MembershipResponse>> getMyMembershipHistory(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      Authentication authentication) {
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(
        membershipApplicationService.getMembershipHistory((String) authentication.getPrincipal(), pageable));
  }

  @GetMapping("/payments/history")
  @PreAuthorize("hasRole('MEMBER')")
  public ResponseEntity<PagedResponse<PaymentResponse>> getMyPayments(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      Authentication authentication) {
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(
        membershipApplicationService.getPaymentHistory((String) authentication.getPrincipal(), pageable));
  }

  private boolean isAdmin(Authentication authentication) {
    return authentication.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
  }

  private boolean isMember(Authentication authentication) {
    return authentication.getAuthorities().stream().anyMatch(a -> "ROLE_MEMBER".equals(a.getAuthority()));
  }
}
