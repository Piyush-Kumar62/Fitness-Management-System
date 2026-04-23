package com.project.fitness.domain.fitness.dto;
import com.project.fitness.domain.fitness.model.WorkoutPlan;

import com.project.fitness.domain.fitness.model.WorkoutPlan.Difficulty;
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
public class WorkoutPlanResponse {

  private String id;
  private String trainerId;
  private String trainerName;
  private String title;
  private String description;
  private Difficulty difficulty;
  private int durationWeeks;
  private int exerciseCount;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private List<ExerciseResponse> exercises;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ExerciseResponse {
    private String id;
    private String name;
    private int sets;
    private int reps;
    private int durationMinutes;
    private String day;
    private int restSeconds;
    private String notes;
  }
}
