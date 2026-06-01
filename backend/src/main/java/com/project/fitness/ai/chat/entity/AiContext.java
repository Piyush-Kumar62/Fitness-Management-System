package com.project.fitness.ai.chat.entity;

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
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "ai_context")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiContext {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "session_id", nullable = false, unique = true)
  private Long sessionId;

  @Column(columnDefinition = "TEXT")
  private String summary;

  @Column(name = "last_message_id")
  private Long lastMessageId;

  @UpdateTimestamp
  private LocalDateTime updatedAt;
}
