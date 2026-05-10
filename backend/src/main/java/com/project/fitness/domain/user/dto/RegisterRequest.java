package com.project.fitness.domain.user.dto;

import com.project.fitness.domain.user.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

  @NotBlank(message = "Email is required")
  @Email(message = "Please provide a valid email address")
  private String email;

  @NotBlank(message = "Password is required")
  @Size(min = 6, message = "Password must be at least 6 characters")
  private String password;

  @NotBlank(message = "First name is required")
  @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
  private String firstName;

  @NotBlank(message = "Last name is required")
  @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
  private String lastName;

  @NotNull(message = "Role is required")
  private UserRole role;

  // Common profile fields
  private String phone;
  private LocalDate dob;
  private String gender;

  // MEMBER-specific: optional gym to join at registration
  private String gymId;

  // OWNER-specific: gym to create at registration
  private String gymName;
  private String gymAddress;
  private String gymContact;
}
