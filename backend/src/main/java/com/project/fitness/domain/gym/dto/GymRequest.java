package com.project.fitness.domain.gym.dto;
import com.project.fitness.domain.gym.model.Gym;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GymRequest {

  @NotBlank(message = "Gym name is required")
  @Size(max = 120, message = "Gym name can be at most 120 characters")
  private String name;

  @NotBlank(message = "Address is required")
  @Size(max = 255, message = "Address can be at most 255 characters")
  private String address;

  @NotBlank(message = "Contact is required")
  @Size(max = 50, message = "Contact can be at most 50 characters")
  private String contact;
}
