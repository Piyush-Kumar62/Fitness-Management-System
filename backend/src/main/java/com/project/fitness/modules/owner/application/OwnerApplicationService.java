package com.project.fitness.modules.owner.application;

import com.project.fitness.common.response.PagedResponse;
import com.project.fitness.domain.gym.repository.GymRepository;
import com.project.fitness.domain.user.dto.UserResponse;
import com.project.fitness.domain.user.model.UserRole;
import com.project.fitness.domain.user.repository.UserRepository;
import com.project.fitness.domain.user.service.DashboardService;
import com.project.fitness.domain.user.service.UserService;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

  private PagedResponse<UserResponse> getOwnerUsersByRole(
      String ownerId, UserRole role, Pageable pageable) {
    List<String> gymIds =
        gymRepository.findByOwnerId(ownerId).stream().map(gym -> gym.getId()).toList();

    if (gymIds.isEmpty()) {
      Page<UserResponse> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
      return PagedResponse.from(emptyPage);
    }

    return PagedResponse.from(
        userRepository
            .findByGymIdInAndRole(gymIds, role, pageable)
            .map(userService::mapToResponse));
  }
}

