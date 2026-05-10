package com.project.fitness.domain.user.dto;

import com.project.fitness.domain.user.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for Owner to create a Trainer under their gym.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTrainerRequest {

  @NotBlank(message = "First name is required")
  private String firstName;

  @NotBlank(message = "Last name is required")
  private String lastName;

  @NotBlank(message = "Email is required")
  @Email(message = "Please provide a valid email address")
  private String email;

  private String phone;

  /** Optional — if omitted, the trainer is assigned to the owner's first gym. */
  private String gymId;
}
