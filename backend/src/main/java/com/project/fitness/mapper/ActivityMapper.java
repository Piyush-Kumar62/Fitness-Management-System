package com.project.fitness.mapper;
import com.project.fitness.domain.user.model.User;

import com.project.fitness.domain.fitness.dto.ActivityRequest;
import com.project.fitness.domain.fitness.dto.ActivityResponse;
import com.project.fitness.domain.fitness.model.Activity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// MapStruct mapper: Activity entity ↔ ActivityRequest/ActivityResponse DTOs.
@Mapper(componentModel = "spring")
public interface ActivityMapper {

  // Map entity to response DTO, derive userId from user.id.
  @Mapping(target = "userId", source = "user.id")
  @Mapping(target = "userName", expression = "java(activity.getUser() != null ? activity.getUser().getFirstName() + \" \" + activity.getUser().getLastName() : \"Member\")")
  ActivityResponse toResponse(Activity activity);

  // Map request to entity; id and timestamps are ignored (generated).
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Activity toEntity(ActivityRequest request);
}
