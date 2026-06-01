package com.project.fitness.ai.websocket.service;

import com.project.fitness.ai.config.AiProperties;
import com.project.fitness.ai.dto.AiChatCommand;
import com.project.fitness.ai.exception.AiValidationException;
import com.project.fitness.ai.prompt.AiPromptService;
import com.project.fitness.ai.rag.AiRagService;
import com.project.fitness.ai.service.port.MemoryPort;
import com.project.fitness.ai.service.port.ProviderPort;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.stereotype.Service;

@Service
public class AiStreamingService {

  private static final int CHUNK_SIZE = 24;

  private final AiPromptService promptService;
  private final AiRagService ragService;
  private final MemoryPort memoryPort;
  private final ProviderPort providerPort;
  private final AiProperties properties;

  public AiStreamingService(
      AiPromptService promptService,
      AiRagService ragService,
      MemoryPort memoryPort,
      ProviderPort providerPort,
      AiProperties properties) {
    this.promptService = promptService;
    this.ragService = ragService;
    this.memoryPort = memoryPort;
    this.providerPort = providerPort;
    this.properties = properties;
  }

  public StreamingResult stream(AiChatCommand command, Consumer<String> chunkConsumer) {
    beforePrompt(command);
    String contextSummary = command.getContextSummary();
    List<com.project.fitness.ai.dto.AiChatMessage> history = memoryPort.loadHistory(
        command.getSessionId(),
        properties.getChat().getMaxHistoryMessages());
    String ragContext = ragService.enrichPrompt(command.getPrompt());
    String builtPrompt = promptService.buildPrompt(
        command.getPrompt(),
        history,
        ragContext,
        command.getSystemPrompt(),
        contextSummary);

    beforeResponse(command);
    ProviderPort.ProviderResult result = providerPort.chatWithFallback(builtPrompt);
    String response = result.reply();
    streamChunks(response, chunkConsumer);
    afterResponse(command);
    return new StreamingResult(result.providerType().name().toLowerCase(), result.model(), response);
  }

  private void streamChunks(String response, Consumer<String> chunkConsumer) {
    if (response == null || response.isBlank()) {
      return;
    }
    long timeoutMs = properties.getChat().getStreamTimeout().toMillis();
    long start = System.currentTimeMillis();
    int index = 0;
    while (index < response.length()) {
      if (System.currentTimeMillis() - start > timeoutMs) {
        throw new AiValidationException("AI stream timed out");
      }
      int end = Math.min(response.length(), index + CHUNK_SIZE);
      String chunk = response.substring(index, end);
      chunkConsumer.accept(chunk);
      index = end;
    }
  }

  protected void beforePrompt(AiChatCommand command) {
    // Hook for future RAG/tooling enhancements.
  }

  protected void beforeToolExecution(AiChatCommand command) {
    // Hook for future tool calling.
  }

  protected void afterToolExecution(AiChatCommand command) {
    // Hook for future tool calling.
  }

  protected void beforeResponse(AiChatCommand command) {
    // Hook for future agent workflows.
  }

  protected void afterResponse(AiChatCommand command) {
    // Hook for future agent workflows.
  }

  public record StreamingResult(String provider, String model, String fullResponse) {
  }
}
