package com.project.fitness.dto;

import java.util.List;

public class RecommendationRequest {
  private String userId;
  private String activityId;
  private String type;
  private String recommendation;
  private List<String> improvements;
  private List<String> suggestions;
  private List<String> safety;

  public RecommendationRequest() {
  }

  public RecommendationRequest(String userId, String activityId, String type,
      String recommendation, List<String> improvements, List<String> suggestions,
      List<String> safety) {
    this.userId = userId;
    this.activityId = activityId;
    this.type = type;
    this.recommendation = recommendation;
    this.improvements = improvements;
    this.suggestions = suggestions;
    this.safety = safety;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getActivityId() {
    return activityId;
  }

  public void setActivityId(String activityId) {
    this.activityId = activityId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getRecommendation() {
    return recommendation;
  }

  public void setRecommendation(String recommendation) {
    this.recommendation = recommendation;
  }

  public List<String> getImprovements() {
    return improvements;
  }

  public void setImprovements(List<String> improvements) {
    this.improvements = improvements;
  }

  public List<String> getSuggestions() {
    return suggestions;
  }

  public void setSuggestions(List<String> suggestions) {
    this.suggestions = suggestions;
  }

  public List<String> getSafety() {
    return safety;
  }

  public void setSafety(List<String> safety) {
    this.safety = safety;
  }
}