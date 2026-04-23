package com.project.fitness.domain.trainer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "attendance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false)
  private String classId;

  @Column(nullable = false)
  private String memberId;

  @Column(nullable = false)
  private String markedBy;

  @Column(nullable = false)
  private LocalDate date;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AttendanceStatus status;

  @CreationTimestamp
  private LocalDateTime createdAt;
}
