package com.project.fitness.ai.chat.service;

import com.project.fitness.ai.chat.entity.ChatSession;
import com.project.fitness.ai.chat.entity.ChatSessionStatus;
import com.project.fitness.ai.chat.repository.ChatSessionRepository;
import com.project.fitness.common.exception.ResourceNotFoundException;
import com.project.fitness.domain.user.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ChatSessionService {

  private final ChatSessionRepository repository;

  public ChatSessionService(ChatSessionRepository repository) {
    this.repository = repository;
  }

  public ChatSession create(String userId, UserRole role, String title) {
    ChatSession session = ChatSession.builder()
        .userId(userId)
        .role(role)
        .title(title)
        .status(ChatSessionStatus.ACTIVE)
        .build();
    return repository.save(session);
  }

  public Page<ChatSession> list(String userId, Pageable pageable) {
    return repository.findByUserIdAndStatusNot(userId, ChatSessionStatus.DELETED, pageable);
  }

  public ChatSession get(String userId, Long sessionId) {
    return repository.findByIdAndUserIdAndStatusNot(sessionId, userId, ChatSessionStatus.DELETED)
        .orElseThrow(() -> new ResourceNotFoundException("ChatSession", "id", sessionId));
  }

  public ChatSession rename(String userId, Long sessionId, String title) {
    ChatSession session = get(userId, sessionId);
    session.setTitle(title);
    return repository.save(session);
  }

  public void softDelete(String userId, Long sessionId) {
    ChatSession session = get(userId, sessionId);
    session.setStatus(ChatSessionStatus.DELETED);
    repository.save(session);
  }
}
