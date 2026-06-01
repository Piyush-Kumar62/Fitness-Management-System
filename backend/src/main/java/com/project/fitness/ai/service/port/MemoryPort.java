package com.project.fitness.ai.service.port;

import com.project.fitness.ai.dto.AiChatMessage;
import java.util.List;

public interface MemoryPort {
  List<AiChatMessage> loadHistory(String sessionId, int maxMessages);
  void append(String sessionId, AiChatMessage message, int maxMessages);
}
