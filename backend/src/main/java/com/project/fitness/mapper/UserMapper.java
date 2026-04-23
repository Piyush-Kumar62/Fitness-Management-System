package com.project.fitness.mapper;

import com.project.fitness.domain.user.dto.UserResponse;
import com.project.fitness.domain.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// MapStruct mapper: User entity ↔ UserResponse DTO. Spring component injection via componentModel = "spring".
@Mapper(componentModel = "spring")
public interface UserMapper {

  // Map all matching fields; exclude sensitive password field.
  @Mapping(target = "id", source = "id")
  @Mapping(target = "email", source = "email")
  @Mapping(target = "firstName", source = "firstName")
  @Mapping(target = "lastName", source = "lastName")
  @Mapping(target = "role", source = "role")
  @Mapping(target = "provider", source = "provider")
  @Mapping(target = "profileImageUrl", source = "profileImageUrl")
  @Mapping(target = "trainerId", source = "trainerId")
  @Mapping(target = "gymId", source = "gymId")
  @Mapping(target = "status", source = "status")
  @Mapping(target = "emailVerified", source = "emailVerified")
  @Mapping(target = "profileComplete", source = "profileComplete")
  @Mapping(target = "active", source = "active")
  @Mapping(target = "dob", source = "dob")
  @Mapping(target = "gender", source = "gender")
  @Mapping(target = "phone", source = "phone")
  @Mapping(target = "createdAt", source = "createdAt")
  @Mapping(target = "updatedAt", source = "updatedAt")
  UserResponse toResponse(User user);
}
