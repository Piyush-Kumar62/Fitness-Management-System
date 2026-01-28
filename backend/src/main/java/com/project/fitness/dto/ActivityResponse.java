package com.project.fitness.dto;

import com.project.fitness.model.ActivityType;
import java.time.LocalDateTime;
import java.util.Map;

public class ActivityResponse {
  private String id;
  private String userId;
  private ActivityType type;
  private Map<String, Object> additionalMetrics;
  private Integer duration;
  private Integer caloriesBurned;
  private LocalDateTime startTime;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public ActivityResponse() {
  }

  public ActivityResponse(String id, String userId, ActivityType type,
      Map<String, Object> additionalMetrics, Integer duration, Integer caloriesBurned,
      LocalDateTime startTime, LocalDateTime createdAt, LocalDateTime updatedAt) {
    this.id = id;
    this.userId = userId;
    this.type = type;
    this.additionalMetrics = additionalMetrics;
    this.duration = duration;
    this.caloriesBurned = caloriesBurned;
    this.startTime = startTime;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
