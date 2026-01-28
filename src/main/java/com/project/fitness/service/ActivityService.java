package com.project.fitness.service;

import com.project.fitness.dto.ActivityRequest;
import com.project.fitness.dto.ActivityResponse;
import com.project.fitness.model.Activity; // Fixes symbol error
import com.project.fitness.model.User;
import com.project.fitness.repository.ActivityRepository;
import com.project.fitness.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityService {
  private final ActivityRepository activityRepo;
  private final UserRepository userRepo;

  public ActivityService(ActivityRepository activityRepo, UserRepository userRepo) {
    this.activityRepo = activityRepo;
    this.userRepo = userRepo;
  }

  public ActivityResponse trackActivity(ActivityRequest request) {
    User user = userRepo.findById(request.getUserId())
        .orElseThrow(() -> new RuntimeException("User not found"));

    Activity activity = Activity.builder()
        .user(user)
        .type(request.getType())
        .duration(request.getDuration())
        .caloriesBurned(request.getCaloriesBurned())
        .startTime(request.getStartTime())
        .additionalMetrics(request.getAdditionalMetrics())
        .build();

    Activity saved = activityRepo.save(activity);
    return mapToResponse(saved);
  }

  public ActivityResponse getActivityById(String id) {
    Activity activity = activityRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("Activity not found: " + id));
    return mapToResponse(activity);
  }

  public List<ActivityResponse> getUserActivities(String userId) {
    return activityRepo.findByUser_Id(userId).stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
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
        activity.getCreatedAt(),
        activity.getUpdatedAt() // Now found via Lombok
    );
  }
}