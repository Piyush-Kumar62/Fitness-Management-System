package com.project.fitness.ai.chat.service;

import com.project.fitness.ai.chat.entity.AiContext;
import com.project.fitness.ai.chat.entity.ChatMessage;
import com.project.fitness.ai.chat.repository.AiContextRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AiContextService {

  private final AiContextRepository repository;
  private final ChatMessageService messageService;

  public AiContextService(AiContextRepository repository, ChatMessageService messageService) {
    this.repository = repository;
    this.messageService = messageService;
  }

  public String getSummary(Long sessionId) {
    return repository.findBySessionId(sessionId)
        .map(AiContext::getSummary)
        .orElse("");
  }

  public void maybeUpdateSummary(Long sessionId, int batchSize, int maxChars) {
    AiContext context = repository.findBySessionId(sessionId)
        .orElse(AiContext.builder().sessionId(sessionId).build());
    Long lastMessageId = context.getLastMessageId();
    List<ChatMessage> batch = messageService.messagesAfter(sessionId, lastMessageId, batchSize);
    if (batch.size() < batchSize) {
      return;
    }
    String newSegment = summarize(batch, maxChars);
    String combined = appendSummary(context.getSummary(), newSegment, maxChars);
    context.setSummary(combined);
    context.setLastMessageId(batch.get(batch.size() - 1).getId());
    repository.save(context);
  }

  private String summarize(List<ChatMessage> messages, int maxChars) {
    if (messages == null || messages.isEmpty()) {
      return "";
    }
    StringBuilder builder = new StringBuilder();
    for (ChatMessage message : messages) {
      builder.append(message.getSender().name().toLowerCase())
          .append(": ")
          .append(message.getContent())
          .append("\n");
    }
    String summary = builder.toString().trim();
    if (summary.length() <= maxChars) {
      return summary;
    }
    return summary.substring(summary.length() - maxChars);
  }

  private String appendSummary(String existing, String segment, int maxChars) {
    if (segment == null || segment.isBlank()) {
      return existing == null ? "" : existing;
    }
    String combined = (existing == null || existing.isBlank())
        ? segment
        : existing + "\n" + segment;
    if (combined.length() <= maxChars) {
      return combined;
    }
    return combined.substring(combined.length() - maxChars);
  }
}
