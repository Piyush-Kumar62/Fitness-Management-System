package com.project.fitness.modules.owner.application;

import com.project.fitness.common.exception.BadRequestException;
import com.project.fitness.common.exception.ResourceNotFoundException;
import com.project.fitness.common.exception.UnauthorizedException;
import com.project.fitness.common.response.PagedResponse;
import com.project.fitness.domain.gym.model.Gym;
import com.project.fitness.domain.gym.repository.GymRepository;
import com.project.fitness.domain.user.dto.CreateTrainerRequest;
import com.project.fitness.domain.user.dto.CreateUserRequest;
import com.project.fitness.domain.user.dto.UserResponse;
import com.project.fitness.domain.user.model.User;
import com.project.fitness.domain.user.model.UserRole;
import com.project.fitness.domain.user.repository.UserRepository;
import com.project.fitness.domain.user.service.DashboardService;
import com.project.fitness.domain.user.service.UserService;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OwnerApplicationService {

  private final GymRepository gymRepository;
  private final UserRepository userRepository;
  private final UserService userService;
  private final DashboardService dashboardService;

  public PagedResponse<UserResponse> getOwnerTrainers(String ownerId, Pageable pageable) {
    return getOwnerUsersByRole(ownerId, UserRole.TRAINER, pageable);
  }

  public PagedResponse<UserResponse> getOwnerMembers(String ownerId, Pageable pageable) {
    return getOwnerUsersByRole(ownerId, UserRole.MEMBER, pageable);
  }

  public Map<String, Object> getOwnerRevenueSummary(String ownerId) {
    return dashboardService.getOwnerDashboardStats(ownerId);
  }

  @Transactional
  public UserResponse createTrainer(String ownerId, CreateTrainerRequest request) {
    String gymId = resolveGymId(ownerId, request.getGymId());
    String tempPassword = UUID.randomUUID().toString().substring(0, 8);
    CreateUserRequest createReq = buildTrainerCreateRequest(request, tempPassword);
    UserResponse created = userService.createUser(createReq);
    patchTrainerDetails(created.getId(), gymId, request.getPhone());
    return userService.mapToResponse(userRepository.findById(created.getId()).orElseThrow());
  }

  private String resolveGymId(String ownerId, String requestedGymId) {
    if (requestedGymId == null || requestedGymId.trim().isEmpty()) {
      List<Gym> gyms = gymRepository.findByOwnerId(ownerId);
      if (gyms.isEmpty()) throw new BadRequestException("You must create a gym first before adding a trainer.");
      return gyms.get(0).getId();
    }
    Gym gym = gymRepository.findById(requestedGymId)
        .orElseThrow(() -> new ResourceNotFoundException("Gym", "id", requestedGymId));
    if (!gym.getOwnerId().equals(ownerId)) throw new UnauthorizedException("You do not have permission to add a trainer to this gym.");
    return requestedGymId;
  }

  private CreateUserRequest buildTrainerCreateRequest(CreateTrainerRequest request, String tempPassword) {
    CreateUserRequest req = new CreateUserRequest();
    req.setFirstName(request.getFirstName().trim());
    req.setLastName(request.getLastName().trim());
    req.setEmail(request.getEmail().trim().toLowerCase());
    req.setPassword(tempPassword);
    req.setRole(UserRole.TRAINER);
    return req;
  }

  private void patchTrainerDetails(String userId, String gymId, String phone) {
    User user = userRepository.findById(userId).orElseThrow();
    user.setGymId(gymId);
    user.setPhone(phone);
    user.setPasswordResetRequired(true);
    userRepository.save(user);
  }

  @Transactional
  public UserResponse assignTrainerToGym(String ownerId, String trainerId, String gymId) {
    validateOwnerGym(ownerId, gymId);
    User trainer = getValidTrainer(trainerId);
    trainer.setGymId(gymId);
    userRepository.save(trainer);
    return userService.mapToResponse(trainer);
  }

  private void validateOwnerGym(String ownerId, String gymId) {
    Gym gym = gymRepository.findById(gymId)
        .orElseThrow(() -> new ResourceNotFoundException("Gym", "id", gymId));
    if (!gym.getOwnerId().equals(ownerId)) throw new UnauthorizedException("You do not have permission to manage this gym.");
  }

  private User getValidTrainer(String trainerId) {
    User trainer = userRepository.findById(trainerId)
        .orElseThrow(() -> new ResourceNotFoundException("Trainer", "id", trainerId));
    if (trainer.getEffectiveRole() != UserRole.TRAINER) throw new BadRequestException("User is not a trainer.");
    return trainer;
  }

  @Transactional
  public void removeTrainer(String ownerId, String trainerId) {
    User trainer = userRepository.findById(trainerId)
        .orElseThrow(() -> new ResourceNotFoundException("Trainer", "id", trainerId));
    assertTrainerBelongsToOwner(trainer, ownerId);
    trainer.setGymId(null);
    trainer.setActive(false);
    userRepository.save(trainer);
  }

  private void assertTrainerBelongsToOwner(User trainer, String ownerId) {
    List<String> gymIds = gymRepository.findByOwnerId(ownerId).stream().map(Gym::getId).toList();
    if (trainer.getGymId() == null || !gymIds.contains(trainer.getGymId())) {
      throw new UnauthorizedException("Trainer does not belong to your gym.");
    }
  }

  private PagedResponse<UserResponse> getOwnerUsersByRole(String ownerId, UserRole role, Pageable pageable) {
    List<String> gymIds = gymRepository.findByOwnerId(ownerId).stream().map(Gym::getId).toList();
    if (gymIds.isEmpty()) return PagedResponse.from(new PageImpl<>(Collections.emptyList(), pageable, 0));
    return PagedResponse.from(userRepository.findByGymIdInAndRole(gymIds, role, pageable).map(userService::mapToResponse));
  }
}
