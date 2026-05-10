package com.project.fitness.modules.fitness.application;

import com.project.fitness.common.response.PagedResponse;
import com.project.fitness.domain.fitness.dto.ActivityRequest;
import com.project.fitness.domain.fitness.dto.ActivityResponse;
import com.project.fitness.domain.fitness.dto.ActivityStatisticsResponse;
import com.project.fitness.domain.fitness.dto.BodyMeasurementRequest;
import com.project.fitness.domain.fitness.dto.BodyMeasurementResponse;
import com.project.fitness.domain.fitness.dto.GoalRequest;
import com.project.fitness.domain.fitness.dto.GoalResponse;
import com.project.fitness.domain.fitness.dto.MilestoneRequest;
import com.project.fitness.domain.fitness.dto.MilestoneResponse;
import com.project.fitness.domain.fitness.dto.RecommendationRequest;
import com.project.fitness.domain.fitness.dto.RecommendationResponse;
import com.project.fitness.domain.fitness.service.ActivityService;
import com.project.fitness.domain.fitness.service.BodyMeasurementService;
import com.project.fitness.domain.fitness.service.GoalService;
import com.project.fitness.domain.fitness.service.RecommendationService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FitnessApplicationService {

  private final ActivityService activityService;
  private final GoalService goalService;
  private final BodyMeasurementService measurementService;
  private final RecommendationService recommendationService;

  public ActivityResponse trackActivity(ActivityRequest request) {
    return activityService.trackActivity(request);
  }

  @Transactional(readOnly = true)
  public ActivityResponse getActivityById(String id) {
    return activityService.getActivityById(id);
  }

  public com.project.fitness.domain.fitness.dto.ActivityResponse updateActivity(String id, String userId, com.project.fitness.domain.fitness.dto.ActivityUpdateRequest request) {
    return activityService.updateActivity(id, userId, request);
  }

  public void deleteActivity(String id, String userId) {
    activityService.deleteActivity(id, userId);
  }

  @Transactional(readOnly = true)
  public PagedResponse<ActivityResponse> getUserActivities(String userId, Pageable pageable) {
    return activityService.getUserActivities(userId, pageable);
  }

  @Transactional(readOnly = true)
  public PagedResponse<ActivityResponse> getAllSystemActivities(Pageable pageable) {
    return activityService.getAllSystemActivities(pageable);
  }

  @Transactional(readOnly = true)
  public PagedResponse<ActivityResponse> searchActivities(
      String userId, String type, String dateFrom, String dateTo, Pageable pageable) {
    return activityService.searchActivities(userId, type, dateFrom, dateTo, pageable);
  }

  @Transactional(readOnly = true)
  public ActivityStatisticsResponse getActivityStatistics(String userId) {
    return activityService.getActivityStatistics(userId);
  }

  public GoalResponse createGoal(GoalRequest request, String userId) {
    return goalService.createGoal(request, userId);
  }

  public GoalResponse updateGoal(String id, GoalRequest request, String userId) {
    return goalService.updateGoal(id, request, userId);
  }

  @Transactional(readOnly = true)
  public GoalResponse getGoalById(String id, String userId) {
    return goalService.getGoalById(id, userId);
  }

  @Transactional(readOnly = true)
  public List<GoalResponse> getUserGoals(String userId) {
    return goalService.getUserGoals(userId);
  }

  public void deleteGoal(String id, String userId) {
    goalService.deleteGoal(id, userId);
  }

  public MilestoneResponse addMilestone(String goalId, MilestoneRequest request, String userId) {
    return goalService.addMilestone(goalId, request, userId);
  }

  public MilestoneResponse achieveMilestone(String milestoneId, String userId) {
    return goalService.achieveMilestone(milestoneId, userId);
  }

  public BodyMeasurementResponse createMeasurement(BodyMeasurementRequest request, String userId) {
    return measurementService.createMeasurement(request, userId);
  }

  public BodyMeasurementResponse updateMeasurement(String id, BodyMeasurementRequest request, String userId) {
    return measurementService.updateMeasurement(id, request, userId);
  }

  @Transactional(readOnly = true)
  public BodyMeasurementResponse getMeasurementById(String id, String userId) {
    return measurementService.getMeasurementById(id, userId);
  }

  @Transactional(readOnly = true)
  public List<BodyMeasurementResponse> getMeasurements(String userId, LocalDate startDate, LocalDate endDate) {
    if (startDate != null && endDate != null) {
      return measurementService.getMeasurementsByDateRange(userId, startDate, endDate);
    }
    return measurementService.getUserMeasurements(userId);
  }

  public void deleteMeasurement(String id, String userId) {
    measurementService.deleteMeasurement(id, userId);
  }

  public RecommendationResponse generateRecommendation(RecommendationRequest request) {
    return recommendationService.generateRecommendation(request);
  }

  @Transactional(readOnly = true)
  public List<RecommendationResponse> getUserRecommendations(String userId) {
    return recommendationService.getUserRecommendations(userId);
  }

  @Transactional(readOnly = true)
  public List<RecommendationResponse> getActivityRecommendations(String activityId) {
    return recommendationService.getActivityRecommendations(activityId);
  }

  @Transactional(readOnly = true)
  public RecommendationResponse getRecommendationById(String id) {
    return recommendationService.getRecommendationById(id);
  }

  public RecommendationResponse createRecommendation(RecommendationRequest request) {
    return recommendationService.createRecommendation(request);
  }

  public void deleteRecommendation(String id) {
    recommendationService.deleteRecommendation(id);
  }
}
