package com.project.fitness.ai.service;

import com.project.fitness.ai.config.AiProperties;
import com.project.fitness.ai.dto.AiChatCommand;
import com.project.fitness.ai.dto.AiChatRequest;
import com.project.fitness.ai.dto.AiChatResponse;
import com.project.fitness.ai.service.port.ChatPort;
import com.project.fitness.ai.exception.AiValidationException;
import org.springframework.stereotype.Service;

@Service
public class AiChatService {

  private final ChatPort chatPort;
  private final AiProperties properties;

  public AiChatService(ChatPort chatPort, AiProperties properties) {
    this.chatPort = chatPort;
    this.properties = properties;
  }

  public AiChatResponse chat(AiChatRequest request, String userId) {
    if (request.getPrompt().length() > properties.getChat().getMaxPromptChars()) {
      throw new AiValidationException("Prompt exceeds maximum length");
    }
    AiChatCommand command = AiChatCommand.builder()
        .prompt(request.getPrompt())
        .sessionId(request.getSessionId())
        .userId(userId)
        .systemPrompt(properties.getPrompt().getSystem())
      .persistMessages(true)
        .build();
    return chatPort.chat(command);
  }

  public AiChatResponse chat(AiChatCommand command) {
    if (command.getPrompt().length() > properties.getChat().getMaxPromptChars()) {
      throw new AiValidationException("Prompt exceeds maximum length");
    }
    if (command.getPersistMessages() == null) {
      command.setPersistMessages(true);
    }
    return chatPort.chat(command);
  }

  public String generateTitle(String prompt, String userId) {
    if (prompt == null || prompt.isBlank()) {
      return null;
    }
    AiChatCommand command = AiChatCommand.builder()
        .prompt("Generate a short title (3-6 words) for: " + prompt)
        .userId(userId)
        .systemPrompt("You generate concise chat titles. Return only the title.")
        .persistMessages(false)
        .build();
    AiChatResponse response = chatPort.chat(command);
    if (response == null || response.getReply() == null) {
      return null;
    }
    return response.getReply().replaceAll("[\n\r]+", " ").trim();
  }
}
