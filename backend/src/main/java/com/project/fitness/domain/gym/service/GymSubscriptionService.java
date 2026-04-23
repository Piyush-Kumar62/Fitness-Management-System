package com.project.fitness.domain.gym.service;
import com.project.fitness.domain.notification.service.IEmailService;
import com.project.fitness.domain.notification.service.NotificationService;
import com.project.fitness.domain.user.model.User;

import com.project.fitness.domain.gym.dto.GymSubscribeRequest;
import com.project.fitness.domain.gym.dto.GymSubscriptionPlanRequest;
import com.project.fitness.domain.gym.dto.GymSubscriptionPlanResponse;
import com.project.fitness.domain.gym.dto.GymSubscriptionResponse;
import com.project.fitness.common.exception.ResourceNotFoundException;
import com.project.fitness.common.exception.UnauthorizedException;
import com.project.fitness.domain.gym.model.Gym;
import com.project.fitness.domain.gym.model.GymSubscription;
import com.project.fitness.domain.gym.model.GymSubscriptionPlan;
import com.project.fitness.domain.gym.model.GymSubscriptionStatus;
import com.project.fitness.domain.gym.repository.GymRepository;
import com.project.fitness.domain.gym.repository.GymSubscriptionPlanRepository;
import com.project.fitness.domain.gym.repository.GymSubscriptionRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GymSubscriptionService {

  private final GymRepository gymRepository;
  private final GymSubscriptionPlanRepository planRepository;
  private final GymSubscriptionRepository subscriptionRepository;
  private final NotificationService notificationService;
  private final IEmailService emailService;

  public GymSubscriptionPlanResponse createPlan(GymSubscriptionPlanRequest request) {
    GymSubscriptionPlan plan = GymSubscriptionPlan.builder()
        .name(request.getName().trim())
        .monthlyPrice(request.getMonthlyPrice())
        .maxMembers(request.getMaxMembers())
        .maxTrainers(request.getMaxTrainers())
        .features(request.getFeatures())
        .active(true)
        .build();
    return toPlanResponse(planRepository.save(plan));
  }

  @Transactional(readOnly = true)
  public List<GymSubscriptionPlanResponse> getPlans(boolean activeOnly) {
    List<GymSubscriptionPlan> plans = activeOnly
        ? planRepository.findByActiveTrueOrderByMonthlyPriceAsc()
        : planRepository.findAll();
    return plans.stream().map(this::toPlanResponse).toList();
  }

  public GymSubscriptionResponse subscribe(String actorId, boolean admin, GymSubscribeRequest request) {
    Gym gym = getGym(request.getGymId());
    if (!admin && !actorId.equals(gym.getOwnerId())) {
      throw new UnauthorizedException("Owners can subscribe only their gyms");
    }
    GymSubscriptionPlan plan = getPlan(request.getPlanId());
    expireExisting(gym.getId());
    GymSubscription subscription = subscriptionRepository.save(GymSubscription.builder()
        .gymId(gym.getId()).planId(plan.getId()).startDate(LocalDate.now())
        .endDate(LocalDate.now().plusMonths(1)).status(GymSubscriptionStatus.ACTIVE).autoRenew(true).build());
    notificationService.notifyUser(gym.getOwnerId(), "SUBSCRIPTION", "Subscription Activated",
        gym.getName() + " subscribed to " + plan.getName() + " plan.");
    
    // Add email confirmation
    emailService.sendGymSubscriptionConfirmation(
        gym.getOwnerId(), // We don't have owner name easily accessible without a user lookup, use ID for now or lookup user
        "Gym Owner", // Fallback name
        gym.getName(),
        plan.getName(),
        plan.getMonthlyPrice().toString(),
        subscription.getEndDate().toString(),
        subscription.getId()
    );
    
    return toSubscriptionResponse(subscription, gym, plan);
  }

  @Transactional(readOnly = true)
  public GymSubscriptionResponse getGymSubscription(String actorId, boolean admin, String gymId) {
    Gym gym = getGym(gymId);
    if (!admin && !actorId.equals(gym.getOwnerId())) {
      throw new UnauthorizedException("Owners can view only their gym subscription");
    }
    GymSubscription subscription = subscriptionRepository
        .findFirstByGymIdAndStatusOrderByCreatedAtDesc(gymId, GymSubscriptionStatus.ACTIVE)
        .orElseThrow(() -> new ResourceNotFoundException("GymSubscription", "gymId", gymId));
    GymSubscriptionPlan plan = getPlan(subscription.getPlanId());
    return toSubscriptionResponse(subscription, gym, plan);
  }

  private void expireExisting(String gymId) {
    subscriptionRepository.findFirstByGymIdAndStatusOrderByCreatedAtDesc(gymId, GymSubscriptionStatus.ACTIVE)
        .ifPresent(subscription -> subscription.setStatus(GymSubscriptionStatus.EXPIRED));
  }

  private Gym getGym(String gymId) {
    return gymRepository.findById(gymId)
        .orElseThrow(() -> new ResourceNotFoundException("Gym", "id", gymId));
  }

  private GymSubscriptionPlan getPlan(String planId) {
    return planRepository.findById(planId)
        .orElseThrow(() -> new ResourceNotFoundException("GymSubscriptionPlan", "id", planId));
  }

  private GymSubscriptionPlanResponse toPlanResponse(GymSubscriptionPlan plan) {
    return GymSubscriptionPlanResponse.builder()
        .id(plan.getId()).name(plan.getName()).monthlyPrice(plan.getMonthlyPrice())
        .maxMembers(plan.getMaxMembers()).maxTrainers(plan.getMaxTrainers())
        .features(plan.getFeatures()).active(plan.isActive()).build();
  }

  private GymSubscriptionResponse toSubscriptionResponse(
      GymSubscription subscription, Gym gym, GymSubscriptionPlan plan) {
    return GymSubscriptionResponse.builder()
        .id(subscription.getId()).gymId(gym.getId()).gymName(gym.getName())
        .planId(plan.getId()).planName(plan.getName())
        .startDate(subscription.getStartDate()).endDate(subscription.getEndDate())
        .status(subscription.getStatus()).autoRenew(subscription.isAutoRenew())
        .build();
  }
}
