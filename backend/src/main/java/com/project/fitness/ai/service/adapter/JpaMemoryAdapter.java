package com.project.fitness.ai.service.adapter;

import com.project.fitness.ai.dto.AiChatMessage;
import com.project.fitness.ai.memory.AiMemoryService;
import com.project.fitness.ai.service.port.MemoryPort;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class JpaMemoryAdapter implements MemoryPort {

  private final AiMemoryService aiMemoryService;

  public JpaMemoryAdapter(AiMemoryService aiMemoryService) {
    this.aiMemoryService = aiMemoryService;
  }

  @Override
  public List<AiChatMessage> loadHistory(String sessionId, int maxMessages) {
    return aiMemoryService.loadHistory(sessionId, maxMessages);
  }

  @Override
  public void append(String sessionId, AiChatMessage message, int maxMessages) {
    aiMemoryService.append(sessionId, message, maxMessages);
  }
}
