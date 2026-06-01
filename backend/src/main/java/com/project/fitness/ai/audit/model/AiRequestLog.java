package com.project.fitness.ai.audit.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "ai_request_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiRequestLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id")
  private String userId;

  @Column(nullable = false, length = 30)
  private String provider;

  @Column(name = "prompt_tokens")
  private Integer promptTokens;

  @Column(name = "response_tokens")
  private Integer responseTokens;

  @Column(name = "latency_ms")
  private Long latencyMs;

  @Column(nullable = false, length = 20)
  private String status;

  @CreationTimestamp
  private LocalDateTime createdAt;
}
