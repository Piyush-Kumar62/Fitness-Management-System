package com.project.fitness.ai.chat.repository;

import com.project.fitness.ai.chat.entity.AiContext;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiContextRepository extends JpaRepository<AiContext, Long> {
  Optional<AiContext> findBySessionId(Long sessionId);
}
