package com.project.fitness.domain.fitness.dto;
import com.project.fitness.domain.fitness.model.DietMeal;

import com.project.fitness.domain.fitness.model.DietMeal.MealType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DietPlanRequest {

  @NotBlank(message = "Title is required")
  private String title;

  private String description;

  @Positive(message = "Target calories must be positive")
  private int targetCalories;

  private int targetProtein;
  private int targetCarbs;
  private int targetFat;

  private List<MealRequest> meals;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class MealRequest {
    private MealType mealType;
    @NotBlank(message = "Meal name is required")
    private String name;
    private int calories;
    private String description;
  }
}
