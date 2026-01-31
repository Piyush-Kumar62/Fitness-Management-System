package com.project.fitness.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BodyMeasurementResponse {
  private String id;
  private String userId;
  private LocalDate measurementDate;
  private Double weight;
  private Double height;
  private Double bodyFat;
  private Double muscleMass;
  private Double bmi;
  private Map<String, Double> measurements;
  private String photoUrl;
  private String notes;
  private LocalDateTime createdAt;
}
