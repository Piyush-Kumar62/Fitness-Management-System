package com.project.fitness.domain.user.dto;
import com.project.fitness.domain.user.model.User;

import com.project.fitness.domain.user.model.AccountStatus;
import com.project.fitness.domain.user.model.UserRole;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

  private String id;
  private String email;
  private String firstName;
  private String lastName;
  private UserRole role;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private String provider;
  private String profileImageUrl;

  // New fields for 3-role system
  private String trainerId;
  private String gymId;
  private AccountStatus status;
  private boolean emailVerified;
  private boolean profileComplete;
  private boolean active;
  private LocalDate dob;
  private String gender;
  private String phone;
}
