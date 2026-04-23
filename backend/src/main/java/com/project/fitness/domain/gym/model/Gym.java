package com.project.fitness.domain.gym.model;

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

import org.hibernate.envers.Audited;

@Entity
@Table(name = "gyms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited
public class Gym {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = false)
  private String ownerId;

  private String address;
  private String contact;

  @Enumerated(EnumType.STRING)
  @Builder.Default
  private GymStatus status = GymStatus.PENDING;

  @CreationTimestamp
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;
}
