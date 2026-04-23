package com.project.fitness.modules.gym.application;

import com.project.fitness.domain.gym.dto.GymRequest;
import com.project.fitness.domain.gym.dto.GymResponse;
import com.project.fitness.domain.gym.dto.GymSubscribeRequest;
import com.project.fitness.domain.gym.dto.GymSubscriptionPlanRequest;
import com.project.fitness.domain.gym.dto.GymSubscriptionPlanResponse;
import com.project.fitness.domain.gym.dto.GymSubscriptionResponse;
import com.project.fitness.domain.gym.service.GymService;
import com.project.fitness.domain.gym.service.GymSubscriptionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GymApplicationService {

  private final GymService gymService;
  private final GymSubscriptionService gymSubscriptionService;

  public GymResponse createGym(String ownerId, GymRequest request) {
    return gymService.createGym(ownerId, request);
  }

  @Transactional(readOnly = true)
  public List<GymResponse> getAllGyms() {
    return gymService.getAllGyms();
  }

  @Transactional(readOnly = true)
  public GymResponse getGymById(String gymId) {
    return gymService.getGymById(gymId);
  }

  @Transactional(readOnly = true)
  public List<GymResponse> getOwnerGyms(String ownerId) {
    return gymService.getOwnerGyms(ownerId);
  }

  public GymResponse updateGym(String gymId, GymRequest request) {
    return gymService.updateGym(gymId, request);
  }

  public void deleteGym(String gymId) {
    gymService.deleteGym(gymId);
  }

  public GymSubscriptionPlanResponse createSubscriptionPlan(GymSubscriptionPlanRequest request) {
    return gymSubscriptionService.createPlan(request);
  }

  @Transactional(readOnly = true)
  public List<GymSubscriptionPlanResponse> getSubscriptionPlans(boolean activeOnly) {
    return gymSubscriptionService.getPlans(activeOnly);
  }

  public GymSubscriptionResponse activateSubscription(
      String actorId, boolean isAdmin, GymSubscribeRequest request) {
    return gymSubscriptionService.subscribe(actorId, isAdmin, request);
  }

  @Transactional(readOnly = true)
  public GymSubscriptionResponse getGymSubscription(String actorId, boolean isAdmin, String gymId) {
    return gymSubscriptionService.getGymSubscription(actorId, isAdmin, gymId);
  }
}
