package com.project.fitness.ai.tool;

import com.project.fitness.ai.config.AiProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.fitness.ai.logging.AiStructuredLogger;
import com.project.fitness.ai.metrics.AiMetricsService;
import com.project.fitness.ai.service.port.ProviderPort;
import com.project.fitness.ai.tool.intent.AiIntentType;
import com.project.fitness.ai.tool.intent.IntentResolver;
import io.micrometer.core.instrument.Timer;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class AiToolDispatcher {

  private final AiProperties properties;
  private final AiToolRegistry toolRegistry;
  private final IntentResolver intentResolver;
  private final ProviderPort providerPort;
  private final AiMetricsService metricsService;
  private final AiStructuredLogger structuredLogger;
  private final ObjectMapper objectMapper;
  private final Map<AiIntentType, String> intentToTool;

  public AiToolDispatcher(
      AiProperties properties,
      AiToolRegistry toolRegistry,
      IntentResolver intentResolver,
      ProviderPort providerPort,
      AiMetricsService metricsService,
      AiStructuredLogger structuredLogger,
      ObjectMapper objectMapper) {
    this.properties = properties;
    this.toolRegistry = toolRegistry;
    this.intentResolver = intentResolver;
    this.providerPort = providerPort;
    this.metricsService = metricsService;
    this.structuredLogger = structuredLogger;
    this.objectMapper = objectMapper;
    this.intentToTool = buildIntentMap();
  }

  public Optional<String> tryHandle(AiToolRequest request) {
    if (!properties.getFeatures().isToolCallingEnabled()) {
      return Optional.empty();
    }
    if (toolRegistry.getTools().isEmpty()) {
      return Optional.empty();
    }
    AiIntentType intent = intentResolver.resolve(request.prompt()).intent();
    if (intent == AiIntentType.GENERAL_CHAT) {
      return Optional.empty();
    }

    String toolName = intentToTool.get(intent);
    if (toolName == null) {
      return Optional.empty();
    }

    AiTool tool = toolRegistry.getTools().get(toolName);
    if (tool == null) {
      return Optional.empty();
    }

    structuredLogger.logToolSelected(request.userId(), request.sessionId(), toolName, intent.value());
    metricsService.recordToolCall(toolName);
    Timer.Sample toolTimer = metricsService.startToolTimer();
    structuredLogger.logToolStarted(request.userId(), request.sessionId(), toolName);

    ToolResult result;
    try {
      ToolExecutionContext context = ToolExecutionContext.of(
          request.userId(),
          request.sessionId(),
          request.prompt());
      result = tool.execute(context);
      metricsService.recordToolSuccess(toolName);
      structuredLogger.logToolCompleted(request.userId(), request.sessionId(), toolName, result.success());
    } catch (RuntimeException ex) {
      metricsService.recordToolFailure(toolName);
      structuredLogger.logToolFailed(request.userId(), request.sessionId(), toolName, ex.getMessage());
      result = new ToolResult(false, toolName, "Tool execution failed", Map.of("error", "Execution failed"));
    } finally {
      metricsService.recordToolDuration(toolName, toolTimer);
    }

    String responsePrompt = buildToolResponsePrompt(request.prompt(), result);
    String reply = providerPort.chatWithFallback(responsePrompt).reply();
    return Optional.ofNullable(reply).map(String::trim).filter(value -> !value.isBlank());
  }

  private Map<AiIntentType, String> buildIntentMap() {
    Map<AiIntentType, String> map = new EnumMap<>(AiIntentType.class);
    map.put(AiIntentType.MEMBERSHIP_LOOKUP, "MembershipTool");
    map.put(AiIntentType.ATTENDANCE_LOOKUP, "AttendanceTool");
    map.put(AiIntentType.TRAINER_LOOKUP, "TrainerTool");
    map.put(AiIntentType.WORKOUT_GENERATION, "WorkoutTool");
    return map;
  }

  private String buildToolResponsePrompt(String userPrompt, ToolResult result) {
    String toolJson;
    try {
      toolJson = objectMapper.writeValueAsString(result);
    } catch (JsonProcessingException ex) {
      toolJson = "{\"success\":false,\"tool\":\"" + result.tool() + "\"}";
    }
    return String.join("\n",
        "System: You are a fitness assistant. Use the tool result JSON to answer the user.",
        "User: " + userPrompt,
        "ToolResult: " + toolJson,
        "Assistant: Respond clearly and concisely.");
  }
}
