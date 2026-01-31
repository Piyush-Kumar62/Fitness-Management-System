package com.project.fitness.service;

import com.project.fitness.dto.RecommendationRequest;
import com.project.fitness.dto.RecommendationResponse;
import com.project.fitness.model.Activity;       // CRITICAL IMPORT
import com.project.fitness.model.Recommendation;
import com.project.fitness.model.User;           // CRITICAL IMPORT
import com.project.fitness.repository.ActivityRepository;
import com.project.fitness.repository.RecommendationRepository;
import com.project.fitness.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RecommendationService {

  private final UserRepository userRepository;
  private final ActivityRepository activityRepository;
  private final RecommendationRepository recommendationRepository;

  public RecommendationService(UserRepository userRepository,
      ActivityRepository activityRepository,
      RecommendationRepository recommendationRepository) {
    this.userRepository = userRepository;
    this.activityRepository = activityRepository;
    this.recommendationRepository = recommendationRepository;
  }

  public RecommendationResponse generateRecommendation(RecommendationRequest request) {
    User user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getUserId()));

    Activity activity = activityRepository.findById(request.getActivityId())
        .orElseThrow(() -> new IllegalArgumentException("Activity not found: " + request.getActivityId()));

    Recommendation recommendation = Recommendation.builder()
        .user(user)
        .activity(activity)
        .type(request.getType())
        .recommendation(request.getRecommendation())
        .improvements(cleanList(request.getImprovements()))
        .suggestions(cleanList(request.getSuggestions()))
        .safety(cleanList(request.getSafety()))
        .build();

    Recommendation saved = recommendationRepository.save(recommendation);
    return mapToResponse(saved);
  }

  public List<RecommendationResponse> getUserRecommendations(String userId) {
    return recommendationRepository.findByUser_IdOrderByCreatedAtDesc(userId)
        .stream().map(this::mapToResponse).toList();
  }

  public List<RecommendationResponse> getActivityRecommendations(String activityId) {
    return recommendationRepository.findByActivity_IdOrderByCreatedAtDesc(activityId)
        .stream().map(this::mapToResponse).toList();
  }

  public RecommendationResponse getRecommendationById(String id) {
    Recommendation rec = recommendationRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Recommendation not found: " + id));
    return mapToResponse(rec);
  }

  public RecommendationResponse createRecommendation(RecommendationRequest request) {
    User user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getUserId()));

    Activity activity = null;
    if (request.getActivityId() != null) {
      activity = activityRepository.findById(request.getActivityId())
          .orElse(null);
    }

    Recommendation recommendation = Recommendation.builder()
        .user(user)
        .activity(activity) // Can be null for manual general recommendations
        .type(request.getType())
        .recommendation(request.getRecommendation())
        .improvements(cleanList(request.getImprovements()))
        .suggestions(cleanList(request.getSuggestions()))
        .safety(cleanList(request.getSafety()))
        .build();

    Recommendation saved = recommendationRepository.save(recommendation);
    return mapToResponse(saved);
  }

  public void deleteRecommendation(String id) {
    if (!recommendationRepository.existsById(id)) {
      throw new IllegalArgumentException("Recommendation not found: " + id);
    }
    recommendationRepository.deleteById(id);
  }

  private RecommendationResponse mapToResponse(Recommendation rec) {
    return RecommendationResponse.builder()
        .id(rec.getId())
        .userId(rec.getUser().getId())
        .activityId(rec.getActivity().getId())
        .type(rec.getType())
        .recommendation(rec.getRecommendation())
        .improvements(rec.getImprovements())
        .suggestions(rec.getSuggestions())
        .safety(rec.getSafety())
        .createdAt(rec.getCreatedAt())
        .updatedAt(rec.getUpdatedAt())
        .build();
  }

  private List<String> cleanList(List<String> list) {
    if (list == null) return List.of();
    return list.stream()
        .filter(s -> s != null && !s.trim().isEmpty())
        .map(String::trim)
        .toList();
  }
}