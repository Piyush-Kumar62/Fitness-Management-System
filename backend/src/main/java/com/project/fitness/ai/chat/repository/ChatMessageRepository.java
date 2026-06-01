package com.project.fitness.ai.chat.repository;

import com.project.fitness.ai.chat.entity.ChatMessage;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
  Page<ChatMessage> findBySessionIdOrderByCreatedAtDesc(Long sessionId, Pageable pageable);

  List<ChatMessage> findTop50BySessionIdOrderByCreatedAtDesc(Long sessionId);

  long countBySessionId(Long sessionId);

  List<ChatMessage> findBySessionIdAndIdGreaterThanOrderByIdAsc(Long sessionId, Long lastMessageId, Pageable pageable);
}
