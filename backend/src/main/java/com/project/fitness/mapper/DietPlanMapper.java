package com.project.fitness.mapper;

import com.project.fitness.domain.fitness.dto.DietPlanResponse;
import com.project.fitness.domain.fitness.dto.DietPlanResponse.MealResponse;
import com.project.fitness.domain.fitness.model.DietMeal;
import com.project.fitness.domain.fitness.model.DietPlan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// MapStruct mapper: DietPlan and DietMeal entities ↔ response DTOs. trainerName is computed externally and must be set after mapping.
@Mapper(componentModel = "spring")
public interface DietPlanMapper {

  // Map DietPlan entity to response DTO; trainerName must be set manually.
  @Mapping(target = "trainerName", ignore = true)
  @Mapping(target = "mealCount", expression = "java(plan.getMeals() == null ? 0 : plan.getMeals().size())")
  DietPlanResponse toResponse(DietPlan plan);

  // Map DietMeal entity to nested MealResponse DTO.
  MealResponse toMealResponse(DietMeal meal);
}
