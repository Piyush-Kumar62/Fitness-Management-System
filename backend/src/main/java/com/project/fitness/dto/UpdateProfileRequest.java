package com.project.fitness.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
  
  @Email(message = "Please provide a valid email address")
  private String email;
  
  @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
  private String firstName;
  
  @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
  private String lastName;
}
