package com.project.fitness.domain.membership.service;
import com.project.fitness.domain.notification.service.IEmailService;
import com.project.fitness.domain.notification.service.NotificationService;

import com.project.fitness.domain.membership.dto.BuyMembershipRequest;
import com.project.fitness.domain.membership.dto.MembershipPlanRequest;
import com.project.fitness.domain.membership.dto.MembershipPlanResponse;
import com.project.fitness.domain.membership.dto.MembershipPurchaseResponse;
import com.project.fitness.domain.membership.dto.MembershipResponse;
import com.project.fitness.domain.payment.dto.PaymentResponse;
import com.project.fitness.common.exception.BadRequestException;
import com.project.fitness.common.exception.ResourceNotFoundException;
import com.project.fitness.common.exception.UnauthorizedException;
import com.project.fitness.domain.gym.model.Gym;
import com.project.fitness.domain.membership.model.Membership;
import com.project.fitness.domain.membership.model.MembershipPlan;
import com.project.fitness.domain.membership.model.MembershipStatus;
import com.project.fitness.domain.payment.model.Payment;
import com.project.fitness.domain.payment.model.PaymentStatus;
import com.project.fitness.domain.user.model.User;
import com.project.fitness.domain.user.model.UserRole;
import com.project.fitness.domain.gym.repository.GymRepository;
import com.project.fitness.domain.membership.repository.MembershipPlanRepository;
import com.project.fitness.domain.membership.repository.MembershipRepository;
import com.project.fitness.domain.payment.repository.PaymentRepository;
import com.project.fitness.domain.user.repository.UserRepository;
import com.project.fitness.common.response.PagedResponse;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MembershipService {

  private final MembershipPlanRepository membershipPlanRepository;
  private final MembershipRepository membershipRepository;
  private final PaymentRepository paymentRepository;
  private final GymRepository gymRepository;
  private final UserRepository userRepository;
  private final NotificationService notificationService;
  private final IEmailService emailService;

  public MembershipPlanResponse createPlan(String actorId, boolean admin, MembershipPlanRequest request) {
    Gym gym = getGym(request.getGymId());
    if (!admin && !gym.getOwnerId().equals(actorId)) {
      throw new UnauthorizedException("You can only create plans for your gym");
    }
    MembershipPlan plan = MembershipPlan.builder()
        .gymId(gym.getId())
        .name(request.getName().trim())
        .price(request.getPrice())
        .durationDays(request.getDurationDays())
        .features(safeTrim(request.getFeatures()))
        .active(true)
        .build();
    return toPlanResponse(membershipPlanRepository.save(plan));
  }

  @Transactional(readOnly = true)
  public List<MembershipPlanResponse> getPlans(String gymId, boolean activeOnly) {
    List<MembershipPlan> plans = activeOnly
        ? membershipPlanRepository.findByGymIdAndActiveTrue(gymId)
        : membershipPlanRepository.findByGymId(gymId);
    return plans.stream().map(this::toPlanResponse).toList();
  }

  public MembershipPurchaseResponse buyMembership(
      String actorId, boolean memberActor, BuyMembershipRequest request) {
    String memberId = resolveMemberId(actorId, memberActor, request.getMemberId());
    User member = getMember(memberId);
    MembershipPlan plan = getPlan(request.getPlanId());
    ensureNoActiveMembership(memberId);
    LocalDate startDate = LocalDate.now();
    Membership membership = membershipRepository.save(Membership.builder()
        .memberId(memberId).planId(plan.getId()).startDate(startDate)
        .endDate(startDate.plusDays(plan.getDurationDays())).status(MembershipStatus.ACTIVE).build());
    Payment payment = paymentRepository.save(Payment.builder()
        .memberId(memberId).membershipId(membership.getId()).amount(plan.getPrice())
        .method(request.getPaymentMethod()).status(PaymentStatus.SUCCESS)
        .transactionId("TXN-" + UUID.randomUUID()).build());
    notificationService.notifyUser(memberId, "MEMBERSHIP", "Membership Activated",
        "Your " + plan.getName() + " plan is active until " + membership.getEndDate() + ".");
    sendMembershipEmails(member, plan, membership, payment, request);
    return buildPurchaseResponse(membership, member, plan, payment);
  }

  private void sendMembershipEmails(User member, MembershipPlan plan, Membership membership,
      Payment payment, BuyMembershipRequest request) {
    String method = request.getPaymentMethod() != null ? request.getPaymentMethod().name() : "UNKNOWN";
    emailService.sendPaymentConfirmation(member.getEmail(), member.getFirstName(),
        plan.getName(), String.format("%.2f", plan.getPrice()),
        payment.getTransactionId(), membership.getEndDate().toString(), method);
    emailService.sendMembershipActivated(member.getEmail(), member.getFirstName(),
        plan.getName(), membership.getStartDate().toString(), membership.getEndDate().toString());
  }

  private MembershipPurchaseResponse buildPurchaseResponse(Membership membership, User member,
      MembershipPlan plan, Payment payment) {
    return MembershipPurchaseResponse.builder()
        .membership(toMembershipResponse(membership, member, plan))
        .payment(toPaymentResponse(payment)).build();
  }

  @Transactional(readOnly = true)
  public PagedResponse<MembershipResponse> getMembershipHistory(String memberId, Pageable pageable) {
    return PagedResponse.from(
        membershipRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable)
            .map(this::toMembershipResponse)
    );
  }

  @Transactional(readOnly = true)
  public PagedResponse<PaymentResponse> getPaymentHistory(String memberId, Pageable pageable) {
    return PagedResponse.from(
        paymentRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable)
            .map(this::toPaymentResponse)
    );
  }

  @Transactional(readOnly = true)
  public MembershipResponse getActiveMembership(String memberId) {
    Membership membership = membershipRepository
        .findFirstByMemberIdAndStatusOrderByEndDateDesc(memberId, MembershipStatus.ACTIVE);
    if (membership == null) {
      return null;
    }
    return toMembershipResponse(membership);
  }

  private String resolveMemberId(String actorId, boolean memberActor, String requestedMemberId) {
    if (!memberActor && requestedMemberId != null && !requestedMemberId.isBlank()) {
      return requestedMemberId;
    }
    if (memberActor && requestedMemberId != null && !requestedMemberId.equals(actorId)) {
      throw new UnauthorizedException("Members can only buy memberships for themselves");
    }
    return actorId;
  }

  private void ensureNoActiveMembership(String memberId) {
    if (membershipRepository.existsByMemberIdAndStatus(memberId, MembershipStatus.ACTIVE)) {
      throw new BadRequestException("Member already has an active membership");
    }
  }

  private Gym getGym(String gymId) {
    return gymRepository.findById(gymId)
        .orElseThrow(() -> new ResourceNotFoundException("Gym", "id", gymId));
  }

  private User getMember(String memberId) {
    User user = userRepository.findById(memberId)
        .orElseThrow(() -> new ResourceNotFoundException("Member", "id", memberId));
    if (user.getRole() != UserRole.MEMBER) {
      throw new BadRequestException("Membership can only be bought for MEMBER users");
    }
    return user;
  }

  private MembershipPlan getPlan(String planId) {
    MembershipPlan plan = membershipPlanRepository.findById(planId)
        .orElseThrow(() -> new ResourceNotFoundException("MembershipPlan", "id", planId));
    if (!plan.isActive()) {
      throw new BadRequestException("Selected membership plan is inactive");
    }
    return plan;
  }

  private String safeTrim(String value) {
    return value == null ? null : value.trim();
  }

  private MembershipPlanResponse toPlanResponse(MembershipPlan plan) {
    return MembershipPlanResponse.builder()
        .id(plan.getId()).gymId(plan.getGymId()).name(plan.getName())
        .price(plan.getPrice()).durationDays(plan.getDurationDays())
        .features(plan.getFeatures()).active(plan.isActive())
        .createdAt(plan.getCreatedAt()).updatedAt(plan.getUpdatedAt())
        .build();
  }

  private MembershipResponse toMembershipResponse(Membership membership) {
    User member = userRepository.findById(membership.getMemberId()).orElse(null);
    MembershipPlan plan = membershipPlanRepository.findById(membership.getPlanId()).orElse(null);
    return toMembershipResponse(membership, member, plan);
  }

  private MembershipResponse toMembershipResponse(Membership membership, User member, MembershipPlan plan) {
    String memberName = member == null ? "Unknown" : member.getFirstName() + " " + member.getLastName();
    String planName = plan == null ? "Unknown" : plan.getName();
    return MembershipResponse.builder()
        .id(membership.getId()).memberId(membership.getMemberId()).memberName(memberName)
        .planId(membership.getPlanId()).planName(planName)
        .startDate(membership.getStartDate()).endDate(membership.getEndDate())
        .status(membership.getStatus()).autoRenew(membership.isAutoRenew())
        .createdAt(membership.getCreatedAt())
        .build();
  }

  private PaymentResponse toPaymentResponse(Payment payment) {
    return PaymentResponse.builder()
        .id(payment.getId()).memberId(payment.getMemberId()).membershipId(payment.getMembershipId())
        .amount(payment.getAmount()).method(payment.getMethod()).status(payment.getStatus())
        .transactionId(payment.getTransactionId()).gateway(payment.getGateway())
        .createdAt(payment.getCreatedAt())
        .build();
  }
}
