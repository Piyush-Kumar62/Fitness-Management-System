package com.project.fitness.domain.fitness.dto;
import com.project.fitness.domain.user.model.FileUpload;

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
