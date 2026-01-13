package com.project.fitness.dto;

import com.project.fitness.model.ActivityType;
import java.time.LocalDateTime;
import java.util.Map;

public class ActivityRequest {
  private String userId;
  private ActivityType type;
  private Map<String, Object> additionalMetrics;
  private Integer duration;
  private Integer caloriesBurned;
  private LocalDateTime startTime;

  public ActivityRequest() {
  }

  public ActivityRequest(String userId, ActivityType type, Map<String, Object> additionalMetrics,
      Integer duration, Integer caloriesBurned, LocalDateTime startTime) {
    this.userId = userId;
    this.type = type;
    this.additionalMetrics = additionalMetrics;
    this.duration = duration;
    this.caloriesBurned = caloriesBurned;
    this.startTime = startTime;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public ActivityType getType() {
    return type;
  }

  public void setType(ActivityType type) {
    this.type = type;
  }

  public Map<String, Object> getAdditionalMetrics() {
    return additionalMetrics;
  }

  public void setAdditionalMetrics(Map<String, Object> additionalMetrics) {
    this.additionalMetrics = additionalMetrics;
  }

  public Integer getDuration() {
    return duration;
  }

  public void setDuration(Integer duration) {
    this.duration = duration;
  }

  public Integer getCaloriesBurned() {
    return caloriesBurned;
  }

  public void setCaloriesBurned(Integer caloriesBurned) {
    this.caloriesBurned = caloriesBurned;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalDateTime startTime) {
    this.startTime = startTime;
  }
}
