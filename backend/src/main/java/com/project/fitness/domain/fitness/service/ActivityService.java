package com.project.fitness.domain.fitness.service;

import com.project.fitness.domain.fitness.dto.ActivityRequest;
import com.project.fitness.domain.fitness.dto.ActivityResponse;
import com.project.fitness.common.exception.ResourceNotFoundException;
import com.project.fitness.domain.fitness.model.Activity;
import com.project.fitness.domain.user.model.User;
import com.project.fitness.domain.fitness.repository.ActivityRepository;
import com.project.fitness.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.project.fitness.common.response.PagedResponse;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivityService {
  private final ActivityRepository activityRepo;
  private final UserRepository userRepo;

  public ActivityResponse trackActivity(ActivityRequest request) {
    User user = userRepo.findById(java.util.Objects.requireNonNull(request.getUserId(), "UserId must not be null"))
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

    Activity activity = Activity.builder()
        .user(user)
        .type(request.getType())
        .duration(request.getDuration())
        .caloriesBurned(request.getCaloriesBurned())
        .startTime(request.getStartTime())
        .date(request.getDate())
        .distance(request.getDistance())
        .intensity(request.getIntensity())
        .notes(request.getNotes())
        .additionalMetrics(request.getAdditionalMetrics())
        .build();

    Activity saved = activityRepo.save(java.util.Objects.requireNonNull(activity, "Activity must not be null"));
    return mapToResponse(saved);
  }

  @Transactional(readOnly = true)
  public ActivityResponse getActivityById(String id) {
    Activity activity = activityRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Activity", "id", id));
    return mapToResponse(activity);
  }

  @Transactional(readOnly = true)
  public PagedResponse<ActivityResponse> getUserActivities(String userId, Pageable pageable) {
    Page<Activity> page = activityRepo.findByUser_Id(userId, pageable);
    return PagedResponse.from(page.map(this::mapToResponse));
  }

  @Transactional(readOnly = true)
  public PagedResponse<ActivityResponse> getAllSystemActivities(Pageable pageable) {
    return PagedResponse.from(activityRepo.findAll(pageable).map(this::mapToResponse));
  }

  @Transactional(readOnly = true)
  public PagedResponse<ActivityResponse> searchActivities(String userId, String type, String dateFrom, String dateTo, Pageable pageable) {
    // For a real app, this should be done in DB level with Criteria API or @Query.
    // Given the previous code, we'll keep the Stream filter but we should ideally paginate there.
    // For simplicity without major DB rewrite, we fallback to finding all and paginating manually or assume findAll works.
    // To properly support Pageable, let's just update the signature.
    List<Activity> activities = activityRepo.findByUser_Id(userId);

    // Filter by type
    if (type != null && !type.isEmpty()) {
      activities = activities.stream()
          .filter(a -> a.getType().name().equalsIgnoreCase(type))
          .collect(Collectors.toList());
    }

    // Filter by date range
    if (dateFrom != null && !dateFrom.isEmpty()) {
      activities = activities.stream()
          .filter(a -> a.getStartTime() != null && 
              !a.getStartTime().isBefore(java.time.LocalDateTime.parse(dateFrom)))
          .collect(Collectors.toList());
    }

    if (dateTo != null && !dateTo.isEmpty()) {
      activities = activities.stream()
          .filter(a -> a.getStartTime() != null && 
              !a.getStartTime().isAfter(java.time.LocalDateTime.parse(dateTo)))
          .collect(Collectors.toList());
    }

    List<ActivityResponse> responseList = activities.stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
    
    // Manual pagination
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), responseList.size());
    List<ActivityResponse> subList = start <= end ? responseList.subList(start, end) : List.of();
    return PagedResponse.from(new org.springframework.data.domain.PageImpl<>(subList, pageable, responseList.size()));
  }

  private ActivityResponse mapToResponse(Activity activity) {
    return new ActivityResponse(
        activity.getId(),
        activity.getUser().getId(),
        activity.getType(),
        activity.getAdditionalMetrics(),
        activity.getDuration(),
        activity.getCaloriesBurned(),
        activity.getStartTime(),
        activity.getDate(),
        activity.getDistance(),
        activity.getIntensity(),
        activity.getNotes(),
        activity.getCreatedAt(),
        activity.getUpdatedAt()
    );
  }
}