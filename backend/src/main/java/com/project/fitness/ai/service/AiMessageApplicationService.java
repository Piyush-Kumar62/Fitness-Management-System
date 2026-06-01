package com.project.fitness.ai.service;

import com.project.fitness.ai.chat.dto.ChatMessagePairResponse;
import com.project.fitness.ai.chat.dto.ChatMessageResponse;
import com.project.fitness.ai.chat.entity.ChatMessage;
import com.project.fitness.ai.chat.entity.AiResponseStatus;
import com.project.fitness.ai.chat.entity.ChatMessageSender;
import com.project.fitness.ai.chat.entity.MessageType;
import com.project.fitness.ai.chat.entity.ChatSession;
import com.project.fitness.ai.chat.service.AiContextService;
import com.project.fitness.ai.chat.service.AiTokenUsageService;
import com.project.fitness.ai.chat.service.ChatMessageService;
import com.project.fitness.ai.chat.service.ChatSessionService;
import com.project.fitness.ai.config.AiProperties;
import com.project.fitness.ai.dto.AiChatCommand;
import com.project.fitness.ai.dto.AiChatResponse;
import com.project.fitness.ai.logging.AiStructuredLogger;
import com.project.fitness.ai.metrics.AiMetricsService;
import com.project.fitness.ai.prompt.PromptTemplateResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AiMessageApplicationService {

  private final ChatSessionService chatSessionService;
  private final ChatMessageService chatMessageService;
  private final AiContextService contextService;
  private final AiTokenUsageService tokenUsageService;
  private final AiChatService aiChatService;
  private final PromptTemplateResolver promptTemplateResolver;
  private final RateLimitService rateLimitService;
  private final AiMetricsService metricsService;
  private final AiStructuredLogger structuredLogger;
  private final AiProperties properties;
  private final TitleSanitizer titleSanitizer;

  public AiMessageApplicationService(
      ChatSessionService chatSessionService,
      ChatMessageService chatMessageService,
      AiContextService contextService,
      AiTokenUsageService tokenUsageService,
      AiChatService aiChatService,
      PromptTemplateResolver promptTemplateResolver,
      RateLimitService rateLimitService,
      AiMetricsService metricsService,
      AiStructuredLogger structuredLogger,
      AiProperties properties,
      TitleSanitizer titleSanitizer) {
    this.chatSessionService = chatSessionService;
    this.chatMessageService = chatMessageService;
    this.contextService = contextService;
    this.tokenUsageService = tokenUsageService;
    this.aiChatService = aiChatService;
    this.promptTemplateResolver = promptTemplateResolver;
    this.rateLimitService = rateLimitService;
    this.metricsService = metricsService;
    this.structuredLogger = structuredLogger;
    this.properties = properties;
    this.titleSanitizer = titleSanitizer;
  }

  public ChatMessagePairResponse sendMessage(String userId, Long sessionId, String content) {
    rateLimitService.checkOrThrow(userId, "ai.chat.message");
    ChatSession session = chatSessionService.get(userId, sessionId);
    String systemPrompt = promptTemplateResolver.resolve(session.getRole());
    String contextSummary = contextService.getSummary(sessionId);

    AiChatCommand command = AiChatCommand.builder()
        .prompt(content)
        .sessionId(String.valueOf(sessionId))
        .userId(userId)
        .systemPrompt(systemPrompt)
        .contextSummary(contextSummary)
        .build();

    var sample = metricsService.startRequest();
    structuredLogger.logRequest(userId, sessionId, "auto");

    AiChatResponse aiResponse;
    long latencyMs = 0L;
    try {
      aiResponse = aiChatService.chat(command);
      String provider = resolveProvider(aiResponse);
      latencyMs = metricsService.recordLatency(sample, provider);
      metricsService.recordSuccess(provider);
    } catch (RuntimeException ex) {
      metricsService.recordFailure("unknown");
      throw ex;
    }

    int promptTokens = tokenUsageService.estimateTokens(content);
    int completionTokens = tokenUsageService.estimateTokens(aiResponse.getReply());
    tokenUsageService.recordUsage(userId, resolveProvider(aiResponse), promptTokens, completionTokens);
    metricsService.recordTokens(resolveProvider(aiResponse), promptTokens + completionTokens);

    ChatMessage userMessage = chatMessageService.addMessage(
        sessionId,
        ChatMessageSender.USER,
      MessageType.USER,
        content,
      promptTokens,
      null);

    ChatMessage assistantMessage = chatMessageService.addMessage(
        sessionId,
        ChatMessageSender.ASSISTANT,
      MessageType.ASSISTANT,
        aiResponse.getReply(),
      completionTokens,
      AiResponseStatus.COMPLETED);

    maybeUpdateContext(sessionId);
    maybeGenerateTitle(userId, session, content);
    structuredLogger.logResponse(userId, sessionId, resolveProvider(aiResponse), latencyMs);

    return ChatMessagePairResponse.builder()
        .userMessage(toResponse(userMessage))
        .assistantMessage(toResponse(assistantMessage))
        .build();
  }

  public Page<ChatMessageResponse> listMessages(String userId, Long sessionId, Pageable pageable) {
    chatSessionService.get(userId, sessionId);
    return chatMessageService.listMessages(sessionId, pageable).map(this::toResponse);
  }

  private void maybeUpdateContext(Long sessionId) {
    int threshold = properties.getContext().getSummaryTriggerMessages();
    contextService.maybeUpdateSummary(sessionId, threshold, properties.getContext().getSummaryMaxChars());
  }

  private void maybeGenerateTitle(String userId, ChatSession session, String prompt) {
    if (session.getTitle() != null && !session.getTitle().isBlank()) {
      return;
    }
    long messageCount = chatMessageService.countMessages(session.getId());
    if (messageCount > 2) {
      return;
    }
    String title = aiChatService.generateTitle(prompt, userId);
    String sanitized = titleSanitizer.sanitize(title);
    chatSessionService.rename(userId, session.getId(), sanitized);
  }

  private ChatMessageResponse toResponse(ChatMessage message) {
    return ChatMessageResponse.builder()
        .id(message.getId())
        .sessionId(message.getSessionId())
        .sender(message.getSender())
        .messageType(message.getMessageType())
        .responseStatus(message.getResponseStatus())
        .content(message.getContent())
        .tokenCount(message.getTokenCount())
        .createdAt(message.getCreatedAt())
        .build();
  }

  private String resolveProvider(AiChatResponse response) {
    if (response == null || response.getProvider() == null) {
      return "unknown";
    }
    return response.getProvider();
  }
}
