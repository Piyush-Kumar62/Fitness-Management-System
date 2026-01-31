package com.project.fitness.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "body_measurements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BodyMeasurement {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private LocalDate measurementDate;

  // Basic measurements
  private Double weight; // in kg
  private Double height; // in cm
  private Double bodyFat; // percentage
  private Double muscleMass; // in kg
  private Double bmi;

  // Detailed measurements (optional)
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "json")
  private Map<String, Double> measurements; // chest, waist, hips, etc.

  // Progress photo
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "photo_id")
  private FileUpload progressPhoto;

  private String notes;

  @CreationTimestamp
  private LocalDateTime createdAt;
}
