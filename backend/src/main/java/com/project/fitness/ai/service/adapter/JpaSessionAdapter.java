package com.project.fitness.ai.service.adapter;

import com.project.fitness.ai.chat.service.ChatSessionService;
import com.project.fitness.domain.user.model.UserRole;
import com.project.fitness.ai.service.port.SessionPort;
import org.springframework.stereotype.Component;

@Component
public class JpaSessionAdapter implements SessionPort {

  private final ChatSessionService chatSessionService;

  public JpaSessionAdapter(ChatSessionService chatSessionService) {
    this.chatSessionService = chatSessionService;
  }

  @Override
  public String ensureSession(String sessionId, String userId, String title) {
    if (sessionId != null && !sessionId.isBlank()) {
      return sessionId;
    }
    Long newSessionId = chatSessionService.create(userId, UserRole.MEMBER, title).getId();
    return String.valueOf(newSessionId);
  }
}
