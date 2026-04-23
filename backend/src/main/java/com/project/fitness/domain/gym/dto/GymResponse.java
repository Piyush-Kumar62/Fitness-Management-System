package com.project.fitness.domain.gym.dto;
import com.project.fitness.domain.gym.model.Gym;

import com.project.fitness.domain.gym.model.GymStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GymResponse {

  private String id;
  private String name;
  private String ownerId;
  private String address;
  private String contact;
  private GymStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
