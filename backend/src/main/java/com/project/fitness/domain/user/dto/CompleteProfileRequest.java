package com.project.fitness.domain.user.dto;

import com.project.fitness.domain.user.model.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompleteProfileRequest {

  @NotNull(message = "Role is required")
  private UserRole role;
}
