package com.project.fitness.modules.membership.application;

import com.project.fitness.common.response.PagedResponse;
import com.project.fitness.domain.membership.dto.BuyMembershipRequest;
import com.project.fitness.domain.membership.dto.MemberCurrentPlansResponse;
import com.project.fitness.domain.membership.dto.MembershipPlanRequest;
import com.project.fitness.domain.membership.dto.MembershipPlanResponse;
import com.project.fitness.domain.membership.dto.MembershipPurchaseResponse;
import com.project.fitness.domain.membership.dto.MembershipResponse;
import com.project.fitness.domain.membership.service.MemberPlanService;
import com.project.fitness.domain.membership.service.MembershipService;
import com.project.fitness.domain.payment.dto.PaymentResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MembershipApplicationService {

  private final MembershipService membershipService;
  private final MemberPlanService memberPlanService;

  public MembershipPlanResponse createPlan(String actorId, boolean admin, MembershipPlanRequest request) {
    return membershipService.createPlan(actorId, admin, request);
  }

  @Transactional(readOnly = true)
  public List<MembershipPlanResponse> getPlans(String gymId, boolean activeOnly) {
    return membershipService.getPlans(gymId, activeOnly);
  }

  public MembershipPurchaseResponse buyMembership(
      String actorId, boolean memberActor, BuyMembershipRequest request) {
    return membershipService.buyMembership(actorId, memberActor, request);
  }

  @Transactional(readOnly = true)
  public PagedResponse<MembershipResponse> getMembershipHistory(String memberId, Pageable pageable) {
    return membershipService.getMembershipHistory(memberId, pageable);
  }

  @Transactional(readOnly = true)
  public PagedResponse<PaymentResponse> getPaymentHistory(String memberId, Pageable pageable) {
    return membershipService.getPaymentHistory(memberId, pageable);
  }

  @Transactional(readOnly = true)
  public MemberCurrentPlansResponse getCurrentPlans(String memberId) {
    return memberPlanService.getCurrentPlans(memberId);
  }
}
