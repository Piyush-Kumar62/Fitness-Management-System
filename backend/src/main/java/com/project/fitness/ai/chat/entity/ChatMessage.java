package com.project.fitness.ai.chat.entity;

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
@Table(name = "chat_message")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "session_id", nullable = false)
  private Long sessionId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private ChatMessageSender sender;

  @Enumerated(EnumType.STRING)
  @Column(name = "message_type", nullable = false, length = 20)
  private MessageType messageType;

  @Enumerated(EnumType.STRING)
  @Column(name = "response_status", length = 20)
  private AiResponseStatus responseStatus;

  @Column(nullable = false, columnDefinition = "LONGTEXT")
  private String content;

  @Column(name = "token_count")
  private Integer tokenCount;

  @CreationTimestamp
  private LocalDateTime createdAt;
}
