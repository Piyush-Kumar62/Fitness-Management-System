package com.project.fitness.domain.trainer.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassScheduleResponse {

  private String id;
  private String gymId;
  private String trainerId;
  private String trainerName;
  private String className;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private int capacity;
  private long bookedCount;
  private long availableSlots;
}
