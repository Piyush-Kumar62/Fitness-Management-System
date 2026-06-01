package com.project.fitness.ai.audit.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "ai_audit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiAuditLog {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(columnDefinition = "uuid")
  private UUID id;

  @Column(name = "user_id")
  private String userId;

  @Column(name = "session_id")
  private String sessionId;

  @Column(nullable = false, length = 30)
  private String provider;

  @Column(nullable = false, length = 80)
  private String model;

  @Column(name = "tokens")
  private Integer tokens;

  @Column(name = "latency_ms")
  private Long latencyMs;

  @Column(nullable = false, length = 20)
  private String status;

  @CreationTimestamp
  private LocalDateTime createdAt;
}
