package com.project.fitness.domain.user.dto;
import com.project.fitness.domain.user.model.User;

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
}
