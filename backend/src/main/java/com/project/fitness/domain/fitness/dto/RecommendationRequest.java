package com.project.fitness.domain.fitness.dto;
import com.project.fitness.domain.fitness.model.Recommendation;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationRequest {
  private String userId;
  private String activityId;
  private String type;
  private String recommendation;
  private List<String> improvements;
  private List<String> suggestions;
  private List<String> safety;
}