package com.project.fitness.domain.user.dto;
import com.project.fitness.domain.user.model.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
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

  private String phone;
  private LocalDate dob;
  private String gender;
}
