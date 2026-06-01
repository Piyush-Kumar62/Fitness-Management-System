package com.project.fitness.ai.chat.dto;

import com.project.fitness.ai.chat.entity.ChatMessageSender;
import com.project.fitness.ai.chat.entity.AiResponseStatus;
import com.project.fitness.ai.chat.entity.MessageType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {

  private Long id;
  private Long sessionId;
  private ChatMessageSender sender;
  private MessageType messageType;
  private AiResponseStatus responseStatus;
  private String content;
  private Integer tokenCount;
  private LocalDateTime createdAt;
}
