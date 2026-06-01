package com.project.fitness.ai.service.adapter;

import com.project.fitness.ai.audit.AiAuditService;
import com.project.fitness.ai.audit.AiRequestLogService;
import com.project.fitness.ai.audit.model.AiAuditLog;
import com.project.fitness.ai.audit.model.AiRequestLog;
import com.project.fitness.ai.config.AiProperties;
import com.project.fitness.ai.dto.AiChatCommand;
import com.project.fitness.ai.dto.AiChatMessage;
import com.project.fitness.ai.dto.AiChatResponse;
import com.project.fitness.ai.prompt.AiPromptService;
import com.project.fitness.ai.rag.AiRagService;
import com.project.fitness.ai.service.port.ChatPort;
import com.project.fitness.ai.service.port.MemoryPort;
import com.project.fitness.ai.service.port.ProviderPort;
import com.project.fitness.ai.service.port.SessionPort;
import com.project.fitness.ai.tool.AiToolDispatcher;
import com.project.fitness.common.exception.BadRequestException;
import com.project.fitness.ai.tool.AiToolRequest;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import com.project.fitness.ai.metrics.AiMetricsService;
import io.micrometer.core.instrument.Timer;

@Component
public class SpringAiChatAdapter implements ChatPort {

  private final AiPromptService promptService;
  private final AiRagService ragService;
  private final MemoryPort memoryPort;
  private final SessionPort sessionPort;
  private final ProviderPort providerPort;
  private final AiToolDispatcher toolDispatcher;
  private final AiProperties properties;
  private final AiAuditService auditService;
  private final AiRequestLogService requestLogService;
  private final AiMetricsService metricsService;

  public SpringAiChatAdapter(
      AiPromptService promptService,
      AiRagService ragService,
      MemoryPort memoryPort,
      SessionPort sessionPort,
      ProviderPort providerPort,
      AiToolDispatcher toolDispatcher,
      AiProperties properties,
      AiAuditService auditService,
      AiRequestLogService requestLogService,
      AiMetricsService metricsService) {
    this.promptService = promptService;
    this.ragService = ragService;
    this.memoryPort = memoryPort;
    this.sessionPort = sessionPort;
    this.providerPort = providerPort;
    this.toolDispatcher = toolDispatcher;
    this.properties = properties;
    this.auditService = auditService;
    this.requestLogService = requestLogService;
    this.metricsService = metricsService;
  }

  @Override
  public AiChatResponse chat(AiChatCommand command) {
    if (!properties.getFeatures().isChatbotEnabled()) {
      throw new BadRequestException("AI chatbot is currently disabled");
    }

    Timer.Sample sample = metricsService.startRequest();

    boolean persistMessages = command.getPersistMessages() == null || command.getPersistMessages();
    String ensuredSessionId = command.getSessionId();
    if (persistMessages) {
      ensuredSessionId = sessionPort.ensureSession(command.getSessionId(), command.getUserId(), command.getPrompt());
    }
    Optional<String> toolReply = Optional.empty();
    if (persistMessages) {
      toolReply = toolDispatcher.tryHandle(new AiToolRequest(
          command.getPrompt(),
          ensuredSessionId,
          command.getUserId()));
    }

    if (toolReply.isPresent()) {
      if (persistMessages) {
        persistMessages(ensuredSessionId, command.getPrompt(), toolReply.get());
      }
      long latency = metricsService.recordLatency(sample, "tool");
      metricsService.recordSuccess("tool");
      saveAudit(command.getUserId(), ensuredSessionId, "tool", "tool", latency, "SUCCESS");
      return AiChatResponse.builder()
          .reply(toolReply.get())
          .source("tool")
          .sessionId(ensuredSessionId)
          .timestamp(Instant.now())
          .build();
    }

    List<AiChatMessage> history = persistMessages
      ? memoryPort.loadHistory(ensuredSessionId, properties.getChat().getMaxHistoryMessages())
      : List.of();
    String ragContext = ragService.enrichPrompt(command.getPrompt());
    String builtPrompt = promptService.buildPrompt(
        command.getPrompt(),
        history,
        ragContext,
        command.getSystemPrompt(),
        command.getContextSummary());

    ProviderPort.ProviderResult providerResult;
    try {
      providerResult = providerPort.chatWithFallback(builtPrompt);
    } catch (RuntimeException ex) {
      metricsService.recordFailure("unknown");
      throw ex;
    }
    if (persistMessages) {
      persistMessages(ensuredSessionId, command.getPrompt(), providerResult.reply());
    }

    String providerName = providerResult.providerType().name().toLowerCase();
    long latency = metricsService.recordLatency(sample, providerName);
    metricsService.recordSuccess(providerName);
    saveAudit(
        command.getUserId(),
        ensuredSessionId,
      providerName,
        providerResult.model(),
        latency,
        "SUCCESS");

    return AiChatResponse.builder()
        .reply(providerResult.reply())
        .provider(providerResult.providerType().name().toLowerCase())
        .model(providerResult.model())
        .source(providerResult.source())
        .sessionId(ensuredSessionId)
        .timestamp(Instant.now())
        .build();
  }

  private void persistMessages(String sessionId, String userPrompt, String assistantReply) {
    AiChatMessage userMessage = AiChatMessage.builder()
        .role(AiChatMessage.Role.USER)
        .content(userPrompt)
        .timestamp(Instant.now())
        .build();
    AiChatMessage assistantMessage = AiChatMessage.builder()
        .role(AiChatMessage.Role.ASSISTANT)
        .content(assistantReply)
        .timestamp(Instant.now())
        .build();

    memoryPort.append(sessionId, userMessage, properties.getChat().getMaxHistoryMessages());
    memoryPort.append(sessionId, assistantMessage, properties.getChat().getMaxHistoryMessages());
  }


  private void saveAudit(String userId, String sessionId, String provider, String model, long latencyMs, String status) {
    if (!properties.getFeatures().isAuditEnabled()) {
      return;
    }
    auditService.save(AiAuditLog.builder()
        .userId(userId)
        .sessionId(sessionId)
        .provider(provider)
        .model(model)
        .tokens(null)
        .latencyMs(latencyMs)
        .status(status)
        .build());
    requestLogService.save(AiRequestLog.builder()
        .userId(userId)
        .provider(provider)
        .promptTokens(null)
        .responseTokens(null)
        .latencyMs(latencyMs)
        .status(status)
        .build());
  }
}
