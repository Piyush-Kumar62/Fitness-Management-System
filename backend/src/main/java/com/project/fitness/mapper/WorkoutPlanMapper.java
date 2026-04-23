package com.project.fitness.mapper;

import com.project.fitness.domain.fitness.dto.WorkoutPlanResponse;
import com.project.fitness.domain.fitness.dto.WorkoutPlanResponse.ExerciseResponse;
import com.project.fitness.domain.fitness.model.WorkoutExercise;
import com.project.fitness.domain.fitness.model.WorkoutPlan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// MapStruct mapper: WorkoutPlan and WorkoutExercise entities ↔ response DTOs. The trainerName field is computed elsewhere; it is ignored here.
@Mapper(componentModel = "spring")
public interface WorkoutPlanMapper {

  // Map WorkoutPlan entity to response DTO. trainerName must be set manually.
  @Mapping(target = "trainerName", ignore = true)
  @Mapping(target = "exerciseCount", expression = "java(plan.getExercises() == null ? 0 : plan.getExercises().size())")
  WorkoutPlanResponse toResponse(WorkoutPlan plan);

  // Map WorkoutExercise entity to nested ExerciseResponse DTO.
  @Mapping(target = "id", source = "id")
  ExerciseResponse toExerciseResponse(WorkoutExercise exercise);
}
