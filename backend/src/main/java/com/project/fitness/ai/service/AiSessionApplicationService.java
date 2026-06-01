package com.project.fitness.ai.service;

import com.project.fitness.ai.chat.dto.ChatSessionResponse;
import com.project.fitness.ai.chat.entity.ChatSession;
import com.project.fitness.ai.chat.service.ChatSessionService;
import com.project.fitness.domain.user.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AiSessionApplicationService {

  private final ChatSessionService chatSessionService;

  public AiSessionApplicationService(ChatSessionService chatSessionService) {
    this.chatSessionService = chatSessionService;
  }

  public ChatSessionResponse create(String userId, UserRole role, String title) {
    ChatSession session = chatSessionService.create(userId, role, title);
    return toResponse(session);
  }

  public Page<ChatSessionResponse> list(String userId, Pageable pageable) {
    return chatSessionService.list(userId, pageable).map(this::toResponse);
  }

  public ChatSessionResponse get(String userId, Long sessionId) {
    return toResponse(chatSessionService.get(userId, sessionId));
  }

  public ChatSessionResponse rename(String userId, Long sessionId, String title) {
    return toResponse(chatSessionService.rename(userId, sessionId, title));
  }

  public void delete(String userId, Long sessionId) {
    chatSessionService.softDelete(userId, sessionId);
  }

  private ChatSessionResponse toResponse(ChatSession session) {
    return ChatSessionResponse.builder()
        .id(session.getId())
        .title(session.getTitle())
        .role(session.getRole())
        .status(session.getStatus())
        .createdAt(session.getCreatedAt())
        .updatedAt(session.getUpdatedAt())
        .build();
  }
}
