package com.project.fitness.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BodyMeasurementRequest {
  @NotNull(message = "Measurement date is required")
  private LocalDate measurementDate;
  
  private Double weight;
  private Double height;
  private Double bodyFat;
  private Double muscleMass;
  private Map<String, Double> measurements;
  private String photoId; // FileUpload ID
  private String notes;
}
