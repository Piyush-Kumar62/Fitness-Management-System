package com.project.fitness.ai.chat.repository;

import com.project.fitness.ai.chat.entity.ChatSession;
import com.project.fitness.ai.chat.entity.ChatSessionStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
  Page<ChatSession> findByUserIdAndStatusNot(String userId, ChatSessionStatus status, Pageable pageable);

  Optional<ChatSession> findByIdAndUserIdAndStatusNot(Long id, String userId, ChatSessionStatus status);
}
