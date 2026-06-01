package com.project.fitness.ai.service.port;

import com.project.fitness.ai.dto.AiChatCommand;
import com.project.fitness.ai.dto.AiChatResponse;

public interface ChatPort {
  AiChatResponse chat(AiChatCommand command);
}
