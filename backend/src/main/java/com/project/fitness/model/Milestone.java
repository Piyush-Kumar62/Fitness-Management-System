package com.project.fitness.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "milestones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Milestone {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "goal_id", nullable = false)
  private Goal goal;

  @Column(nullable = false)
  private String title;

  private String description;

  @Column(nullable = false)
  private Double targetValue;

  @Builder.Default
  private Boolean achieved = false;

  private LocalDateTime achievedAt;

  @CreationTimestamp
  private LocalDateTime createdAt;
}
