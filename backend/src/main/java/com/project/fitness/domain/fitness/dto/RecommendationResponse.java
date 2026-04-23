package com.project.fitness.domain.fitness.dto;
import com.project.fitness.domain.fitness.model.Recommendation;

import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponse {
  private String id;
  private String userId;
  private String activityId;
  private String type;
  private String recommendation;
  private List<String> improvements;
  private List<String> suggestions;
  private List<String> safety;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}