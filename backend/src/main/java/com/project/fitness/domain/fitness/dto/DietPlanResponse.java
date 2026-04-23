package com.project.fitness.domain.fitness.dto;
import com.project.fitness.domain.fitness.model.DietMeal;

import com.project.fitness.domain.fitness.model.DietMeal.MealType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DietPlanResponse {

  private String id;
  private String trainerId;
  private String trainerName;
  private String title;
  private String description;
  private int targetCalories;
  private int targetProtein;
  private int targetCarbs;
  private int targetFat;
  private int mealCount;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private List<MealResponse> meals;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class MealResponse {
    private String id;
    private MealType mealType;
    private String name;
    private int calories;
    private String description;
  }
}
