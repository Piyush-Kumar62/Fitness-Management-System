package com.project.fitness.ai.prompt;

import com.project.fitness.ai.config.AiProperties;
import com.project.fitness.ai.dto.AiChatMessage;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AiPromptService {

  private final AiProperties properties;

  public AiPromptService(AiProperties properties) {
    this.properties = properties;
  }

  public String buildPrompt(String prompt, List<AiChatMessage> history, String ragContext,
      String systemPrompt, String contextSummary) {
    StringBuilder builder = new StringBuilder();
    String resolvedSystem = systemPrompt == null || systemPrompt.isBlank()
        ? properties.getPrompt().getSystem()
        : systemPrompt;
    builder.append("System: ").append(resolvedSystem).append("\n");
    if (contextSummary != null && !contextSummary.isBlank()) {
      builder.append("ContextSummary: ").append(contextSummary).append("\n");
    }
    if (ragContext != null && !ragContext.isBlank()) {
      builder.append("Context: ").append(ragContext).append("\n");
    }
    for (AiChatMessage message : history) {
      builder.append(message.getRole().name().toLowerCase())
          .append(": ")
          .append(message.getContent())
          .append("\n");
    }
    builder.append("user: ").append(prompt);
    return trimToMax(builder.toString(), properties.getChat().getMaxPromptChars());
  }

  private String trimToMax(String input, int maxChars) {
    if (input.length() <= maxChars) {
      return input;
    }
    return input.substring(input.length() - maxChars);
  }
}
