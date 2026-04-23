package com.project.fitness.domain.user.dto;

import com.project.fitness.domain.user.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

  @Email(message = "Please provide a valid email address")
  private String email;

  @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
  private String firstName;

  @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
  private String lastName;

  @Size(min = 8, message = "Password must be at least 8 characters long")
  private String password;

  private UserRole role;

  private Boolean active;
}
