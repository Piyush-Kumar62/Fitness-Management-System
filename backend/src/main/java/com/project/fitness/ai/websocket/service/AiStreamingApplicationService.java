package com.project.fitness.ai.websocket.service;

import com.project.fitness.ai.chat.entity.AiResponseStatus;
import com.project.fitness.ai.chat.entity.ChatMessage;
import com.project.fitness.ai.chat.entity.ChatMessageSender;
import com.project.fitness.ai.chat.entity.ChatSession;
import com.project.fitness.ai.chat.entity.MessageType;
import com.project.fitness.ai.chat.service.AiContextService;
import com.project.fitness.ai.chat.service.AiTokenUsageService;
import com.project.fitness.ai.chat.service.ChatMessageService;
import com.project.fitness.ai.chat.service.ChatSessionService;
import com.project.fitness.ai.dto.AiChatCommand;
import com.project.fitness.ai.exception.AiValidationException;
import com.project.fitness.ai.logging.AiStructuredLogger;
import com.project.fitness.ai.metrics.AiMetricsService;
import com.project.fitness.ai.prompt.PromptTemplateResolver;
import com.project.fitness.ai.service.AiFeatureFlagService;
import com.project.fitness.ai.service.RateLimitService;
import com.project.fitness.domain.user.model.UserRole;
import com.project.fitness.common.exception.UnauthorizedException;
import com.project.fitness.ai.websocket.dto.AiStreamRequest;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

@Service
public class AiStreamingApplicationService {

  private final ChatSessionService chatSessionService;
  private final ChatMessageService chatMessageService;
  private final AiContextService contextService;
  private final AiTokenUsageService tokenUsageService;
  private final PromptTemplateResolver promptTemplateResolver;
  private final RateLimitService rateLimitService;
  private final AiStreamPublisher streamPublisher;
  private final AiStreamingService streamingService;
  private final AiStreamingMetricsService streamingMetricsService;
  private final AiStructuredLogger structuredLogger;
  private final AiMetricsService metricsService;
  private final com.project.fitness.ai.config.AiProperties properties;
  private final AiFeatureFlagService featureFlagService;

  public AiStreamingApplicationService(
      ChatSessionService chatSessionService,
      ChatMessageService chatMessageService,
      AiContextService contextService,
      AiTokenUsageService tokenUsageService,
      PromptTemplateResolver promptTemplateResolver,
      RateLimitService rateLimitService,
      AiStreamPublisher streamPublisher,
      AiStreamingService streamingService,
      AiStreamingMetricsService streamingMetricsService,
      AiStructuredLogger structuredLogger,
      AiMetricsService metricsService,
      com.project.fitness.ai.config.AiProperties properties,
      AiFeatureFlagService featureFlagService) {
    this.chatSessionService = chatSessionService;
    this.chatMessageService = chatMessageService;
    this.contextService = contextService;
    this.tokenUsageService = tokenUsageService;
    this.promptTemplateResolver = promptTemplateResolver;
    this.rateLimitService = rateLimitService;
    this.streamPublisher = streamPublisher;
    this.streamingService = streamingService;
    this.streamingMetricsService = streamingMetricsService;
    this.structuredLogger = structuredLogger;
    this.metricsService = metricsService;
    this.properties = properties;
    this.featureFlagService = featureFlagService;
  }

  public void stream(AiStreamRequest request, String userId, UserRole role) {
    if (!featureFlagService.isChatEnabled()) {
      throw new AiValidationException("AI chat is disabled");
    }
    if (request.getMessage().length() > properties.getChat().getMaxPromptChars()) {
      throw new AiValidationException("Message exceeds maximum length");
    }
    rateLimitService.checkOrThrow(userId, "ai.stream.chat");
    ChatSession session = chatSessionService.get(userId, request.getSessionId());
    validateRole(role, session.getRole());

    String systemPrompt = promptTemplateResolver.resolve(session.getRole());
    String contextSummary = contextService.getSummary(request.getSessionId());

    ChatMessage userMessage = chatMessageService.addMessage(
        request.getSessionId(),
        ChatMessageSender.USER,
        MessageType.USER,
        request.getMessage(),
        tokenUsageService.estimateTokens(request.getMessage()),
        null);

    ChatMessage assistantMessage = chatMessageService.addMessage(
        request.getSessionId(),
        ChatMessageSender.ASSISTANT,
        MessageType.ASSISTANT,
        "",
        null,
        AiResponseStatus.PENDING);

    AiChatCommand command = AiChatCommand.builder()
        .prompt(request.getMessage())
        .sessionId(String.valueOf(request.getSessionId()))
        .userId(userId)
        .systemPrompt(systemPrompt)
        .contextSummary(contextSummary)
        .persistMessages(false)
        .build();

    Timer.Sample streamTimer = streamingMetricsService.startStreamTimer();
    long startMillis = System.currentTimeMillis();
    metricsService.startRequest();
    structuredLogger.logRequest(userId, request.getSessionId(), "stream");

    StringBuilder buffer = new StringBuilder();
    chatMessageService.updateMessageStatus(assistantMessage.getId(), AiResponseStatus.STREAMING);

    try {
      AiStreamingService.StreamingResult result = streamingService.stream(command, chunk -> {
        buffer.append(chunk);
        streamPublisher.publishChunk(request.getSessionId(), assistantMessage.getId(), chunk);
      });

      String response = buffer.toString();
      int completionTokens = tokenUsageService.estimateTokens(response);
      int promptTokens = tokenUsageService.estimateTokens(request.getMessage());
      tokenUsageService.recordUsage(userId, result.provider(),
          promptTokens, completionTokens);
      metricsService.recordTokens(result.provider(), promptTokens + completionTokens);
        metricsService.recordSuccess(result.provider());

      chatMessageService.updateMessageContentAndStatus(
          assistantMessage.getId(),
          response,
          AiResponseStatus.COMPLETED);

      maybeUpdateContext(request.getSessionId());
      streamPublisher.publishComplete(request.getSessionId(), assistantMessage.getId());
      streamingMetricsService.onStreamCompleted();
      streamingMetricsService.recordStreamDuration(streamTimer);
      long latencyMs = System.currentTimeMillis() - startMillis;
      structuredLogger.logResponse(userId, request.getSessionId(), result.provider(), latencyMs);
    } catch (Exception ex) {
      metricsService.recordFailure("stream");
      chatMessageService.updateMessageContentAndStatus(
          assistantMessage.getId(),
          "Unable to generate response",
          AiResponseStatus.FAILED);
      streamPublisher.publishError(request.getSessionId(), assistantMessage.getId(), "Unable to generate response");
      streamPublisher.publishComplete(request.getSessionId(), assistantMessage.getId());
      streamingMetricsService.onStreamFailed();
      streamingMetricsService.recordStreamDuration(streamTimer);
      structuredLogger.logResponse(userId, request.getSessionId(), "stream", System.currentTimeMillis() - startMillis);
      throw ex;
    }
  }

  private void validateRole(UserRole requestRole, UserRole sessionRole) {
    if (requestRole == null || sessionRole == null) {
      throw new UnauthorizedException("Role validation failed");
    }
    if (requestRole != sessionRole && requestRole != UserRole.ADMIN) {
      throw new UnauthorizedException("Role validation failed");
    }
  }

  private void maybeUpdateContext(Long sessionId) {
    int threshold = properties.getContext().getSummaryTriggerMessages();
    contextService.maybeUpdateSummary(sessionId, threshold, properties.getContext().getSummaryMaxChars());
  }
}
