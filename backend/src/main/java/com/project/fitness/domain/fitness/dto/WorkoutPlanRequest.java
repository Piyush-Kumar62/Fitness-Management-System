package com.project.fitness.domain.fitness.dto;
import com.project.fitness.domain.fitness.model.WorkoutPlan;

import com.project.fitness.domain.fitness.model.WorkoutPlan.Difficulty;
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
public class WorkoutPlanRequest {

  @NotBlank(message = "Title is required")
  private String title;

  private String description;
  private Difficulty difficulty;

  @Positive(message = "Duration must be positive")
  private int durationWeeks;

  private List<ExerciseRequest> exercises;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ExerciseRequest {
    @NotBlank(message = "Exercise name is required")
    private String name;
    private int sets;
    private int reps;
    private int durationMinutes;
    private String day;
    private int restSeconds;
    private String notes;
  }
}
