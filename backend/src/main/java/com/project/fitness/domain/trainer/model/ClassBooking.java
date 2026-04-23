package com.project.fitness.domain.trainer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "class_bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassBooking {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false)
  private String classId;

  @Column(nullable = false)
  private String memberId;

  @Enumerated(EnumType.STRING)
  @Builder.Default
  private ClassBookingStatus status = ClassBookingStatus.BOOKED;

  @CreationTimestamp
  private LocalDateTime bookedAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;
}
