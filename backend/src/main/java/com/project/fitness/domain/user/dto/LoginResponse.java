package com.project.fitness.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
  private String token;
  private UserResponse user;
  /** True when the user is logging in for the first time with a temporary password. */
  private boolean passwordResetRequired;
}
