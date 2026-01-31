package com.project.fitness.dto;

import com.project.fitness.model.ActivityType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;

public class ActivityRequest {
  private String userId;
  
  @NotNull(message = "Activity type is required")
  private ActivityType type;
  
  private Map<String, Object> additionalMetrics;
  
  @NotNull(message = "Duration is required")
  @Min(value = 1, message = "Duration must be at least 1 minute")
  private Integer duration;
  
  @NotNull(message = "Calories burned is required")
  @Min(value = 0, message = "Calories burned cannot be negative")
  private Integer caloriesBurned;
  
  private LocalDateTime startTime;
  private LocalDateTime date;
  private Double distance; // in km
  private String intensity;
  private String notes;

  public ActivityRequest() {
  }

  public ActivityRequest(String userId, ActivityType type, Map<String, Object> additionalMetrics,
      Integer duration, Integer caloriesBurned, LocalDateTime startTime, LocalDateTime date,
      Double distance, String intensity, String notes) {
    this.userId = userId;
    this.type = type;
    this.additionalMetrics = additionalMetrics;
    this.duration = duration;
    this.caloriesBurned = caloriesBurned;
    this.startTime = startTime;
    this.date = date;
    this.distance = distance;
    this.intensity = intensity;
    this.notes = notes;
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

  public LocalDateTime getDate() {
    return date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  public Double getDistance() {
    return distance;
  }

  public void setDistance(Double distance) {
    this.distance = distance;
  }

  public String getIntensity() {
    return intensity;
  }

  public void setIntensity(String intensity) {
    this.intensity = intensity;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }
}
