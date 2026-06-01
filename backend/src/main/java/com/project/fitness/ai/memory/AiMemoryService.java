package com.project.fitness.ai.memory;

import com.project.fitness.ai.chat.entity.AiResponseStatus;
import com.project.fitness.ai.chat.entity.ChatMessageSender;
import com.project.fitness.ai.chat.entity.MessageType;
import com.project.fitness.ai.chat.service.AiTokenUsageService;
import com.project.fitness.ai.chat.service.ChatMessageService;
import com.project.fitness.ai.config.AiProperties;
import com.project.fitness.ai.dto.AiChatMessage;
import com.project.fitness.ai.exception.AiValidationException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AiMemoryService {

  private final AiProperties properties;
  private final ChatMessageService chatMessageService;
  private final AiTokenUsageService tokenUsageService;

  public AiMemoryService(AiProperties properties,
      ChatMessageService chatMessageService,
      AiTokenUsageService tokenUsageService) {
    this.properties = properties;
    this.chatMessageService = chatMessageService;
    this.tokenUsageService = tokenUsageService;
  }

  public List<AiChatMessage> loadHistory(String sessionId, int maxMessages) {
    if (!properties.getFeatures().isMemoryEnabled()) {
      return List.of();
    }
    Long resolvedSessionId = parseSessionId(sessionId);
    if (resolvedSessionId == null) {
      return List.of();
    }
    return chatMessageService.recentMessages(resolvedSessionId, maxMessages).stream()
        .map(message -> AiChatMessage.builder()
            .role(convertRole(message.getSender()))
            .content(message.getContent())
            .timestamp(message.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant())
            .build())
        .toList();
  }

  public void append(String sessionId, AiChatMessage message, int maxMessages) {
    if (!properties.getFeatures().isMemoryEnabled()) {
      return;
    }
    Long resolvedSessionId = parseSessionId(sessionId);
    if (resolvedSessionId == null) {
      throw new AiValidationException("Invalid session id");
    }
    int tokenCount = tokenUsageService.estimateTokens(message.getContent());
    chatMessageService.addMessage(
        resolvedSessionId,
        convertSender(message.getRole()),
      resolveMessageType(message.getRole()),
        message.getContent(),
      tokenCount,
      resolveStatus(message.getRole()));
  }

  private Long parseSessionId(String sessionId) {
    if (sessionId == null || sessionId.isBlank()) {
      return null;
    }
    try {
      return Long.parseLong(sessionId);
    } catch (NumberFormatException ex) {
      return null;
    }
  }

  private AiChatMessage.Role convertRole(ChatMessageSender sender) {
    return switch (sender) {
      case USER -> AiChatMessage.Role.USER;
      case ASSISTANT -> AiChatMessage.Role.ASSISTANT;
      case SYSTEM -> AiChatMessage.Role.SYSTEM;
    };
  }

  private ChatMessageSender convertSender(AiChatMessage.Role role) {
    return switch (role) {
      case USER -> ChatMessageSender.USER;
      case ASSISTANT -> ChatMessageSender.ASSISTANT;
      case SYSTEM -> ChatMessageSender.SYSTEM;
    };
  }

  private MessageType resolveMessageType(AiChatMessage.Role role) {
    return switch (role) {
      case USER -> MessageType.USER;
      case ASSISTANT -> MessageType.ASSISTANT;
      case SYSTEM -> MessageType.SYSTEM;
    };
  }

  private AiResponseStatus resolveStatus(AiChatMessage.Role role) {
    return role == AiChatMessage.Role.ASSISTANT ? AiResponseStatus.COMPLETED : null;
  }
}
