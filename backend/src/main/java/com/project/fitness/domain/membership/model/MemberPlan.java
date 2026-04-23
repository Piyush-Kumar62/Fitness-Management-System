package com.project.fitness.domain.membership.model;

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

@Entity
@Table(name = "member_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberPlan {

  public enum PlanStatus {
    ACTIVE, COMPLETED, CANCELLED
  }

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false)
  private String memberId;

  private String workoutPlanId;
  private String dietPlanId;

  @Column(nullable = false)
  private String assignedBy; // trainer ID

  @Enumerated(EnumType.STRING)
  @Builder.Default
  private PlanStatus status = PlanStatus.ACTIVE;

  @CreationTimestamp
  private LocalDateTime assignedAt;
}
