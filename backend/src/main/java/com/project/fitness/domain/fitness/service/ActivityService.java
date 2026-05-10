package com.project.fitness.domain.fitness.service;

import com.project.fitness.domain.fitness.dto.ActivityRequest;
import com.project.fitness.domain.fitness.dto.ActivityResponse;
import com.project.fitness.domain.fitness.dto.ActivityStatisticsResponse;
import com.project.fitness.common.exception.ResourceNotFoundException;
import com.project.fitness.domain.fitness.model.Activity;
import com.project.fitness.domain.fitness.model.ActivityType;
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
import java.util.Map;
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

  public ActivityResponse updateActivity(String id, String userId, com.project.fitness.domain.fitness.dto.ActivityUpdateRequest request) {
    Activity activity = activityRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Activity", "id", id));
    if (!activity.getUser().getId().equals(userId)) {
      throw new com.project.fitness.common.exception.UnauthorizedException("You can only edit your own activities");
    }
    if (request.getType() != null) activity.setType(request.getType());
    if (request.getDuration() != null) activity.setDuration(request.getDuration());
    if (request.getCaloriesBurned() != null) activity.setCaloriesBurned(request.getCaloriesBurned());
    if (request.getStartTime() != null) activity.setStartTime(request.getStartTime());
    if (request.getDate() != null) activity.setDate(request.getDate());
    if (request.getDistance() != null) activity.setDistance(request.getDistance());
    if (request.getIntensity() != null) activity.setIntensity(request.getIntensity());
    if (request.getNotes() != null) activity.setNotes(request.getNotes());
    if (request.getAdditionalMetrics() != null) activity.setAdditionalMetrics(request.getAdditionalMetrics());
    return mapToResponse(activityRepo.save(activity));
  }

  public void deleteActivity(String id, String userId) {
    Activity activity = activityRepo.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Activity", "id", id));
    if (!activity.getUser().getId().equals(userId)) {
      throw new com.project.fitness.common.exception.UnauthorizedException("You can only delete your own activities");
    }
    activityRepo.delete(activity);
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
    List<Activity> activities = activityRepo.findByUser_Id(userId);
    activities = applyTypeFilter(activities, type);
    activities = applyDateFilter(activities, dateFrom, dateTo);
    List<ActivityResponse> responseList = activities.stream().map(this::mapToResponse).collect(Collectors.toList());
    int start = (int) pageable.getOffset();
    int end = Math.min(start + pageable.getPageSize(), responseList.size());
    List<ActivityResponse> page = start <= end ? responseList.subList(start, end) : List.of();
    return PagedResponse.from(new org.springframework.data.domain.PageImpl<>(page, pageable, responseList.size()));
  }

  private List<Activity> applyTypeFilter(List<Activity> activities, String type) {
    if (type == null || type.isEmpty()) return activities;
    return activities.stream().filter(a -> a.getType().name().equalsIgnoreCase(type)).collect(Collectors.toList());
  }

  private List<Activity> applyDateFilter(List<Activity> activities, String from, String to) {
    if (from != null && !from.isEmpty()) {
      java.time.LocalDateTime fromDt = java.time.LocalDateTime.parse(from);
      activities = activities.stream().filter(a -> a.getStartTime() != null && !a.getStartTime().isBefore(fromDt)).collect(Collectors.toList());
    }
    if (to != null && !to.isEmpty()) {
      java.time.LocalDateTime toDt = java.time.LocalDateTime.parse(to);
      activities = activities.stream().filter(a -> a.getStartTime() != null && !a.getStartTime().isAfter(toDt)).collect(Collectors.toList());
    }
    return activities;
  }

  @Transactional(readOnly = true)
  public ActivityStatisticsResponse getActivityStatistics(String userId) {
    List<Activity> activities = activityRepo.findByUser_Id(userId);
    if (activities.isEmpty()) return buildEmptyStats();
    long total = activities.size();
    long totalDuration = activities.stream().mapToLong(Activity::getDuration).sum();
    long totalCalories = activities.stream().mapToLong(Activity::getCaloriesBurned).sum();
    ActivityType mostCommon = computeMostCommonType(activities);
    return ActivityStatisticsResponse.builder()
        .totalActivities(total).totalDuration(totalDuration).totalCalories(totalCalories)
        .averageDuration((double) totalDuration / total).averageCalories((double) totalCalories / total)
        .mostCommonType(mostCommon).build();
  }

  private ActivityStatisticsResponse buildEmptyStats() {
    return ActivityStatisticsResponse.builder()
        .totalActivities(0).totalDuration(0).totalCalories(0)
        .averageDuration(0).averageCalories(0).mostCommonType(ActivityType.OTHER).build();
  }

  private ActivityType computeMostCommonType(List<Activity> activities) {
    return activities.stream()
        .collect(Collectors.groupingBy(Activity::getType, Collectors.counting()))
        .entrySet().stream().max(Map.Entry.comparingByValue())
        .map(Map.Entry::getKey).orElse(ActivityType.OTHER);
  }

  private ActivityResponse mapToResponse(Activity activity) {
    User user = activity.getUser();
    String userName = (user != null) ? user.getFirstName() + " " + user.getLastName() : "Member";

    return ActivityResponse.builder()
        .id(activity.getId())
        .userId(user != null ? user.getId() : null)
        .userName(userName)
        .type(activity.getType())
        .additionalMetrics(activity.getAdditionalMetrics())
        .duration(activity.getDuration())
        .caloriesBurned(activity.getCaloriesBurned())
        .startTime(activity.getStartTime())
        .date(activity.getDate())
        .distance(activity.getDistance())
        .intensity(activity.getIntensity())
        .notes(activity.getNotes())
        .createdAt(activity.getCreatedAt())
        .updatedAt(activity.getUpdatedAt())
        .build();
  }
}
