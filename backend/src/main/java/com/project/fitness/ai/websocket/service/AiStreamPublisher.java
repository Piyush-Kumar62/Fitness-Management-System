package com.project.fitness.ai.websocket.service;

import com.project.fitness.ai.websocket.dto.AiStreamChunk;
import com.project.fitness.ai.websocket.dto.AiStreamComplete;
import com.project.fitness.ai.websocket.dto.StreamPayloadType;
import java.time.Instant;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class AiStreamPublisher {

  private final SimpMessagingTemplate messagingTemplate;
  private final AiStreamingMetricsService metricsService;

  public AiStreamPublisher(SimpMessagingTemplate messagingTemplate, AiStreamingMetricsService metricsService) {
    this.messagingTemplate = messagingTemplate;
    this.metricsService = metricsService;
  }

  public void publishChunk(Long sessionId, Long messageId, String content) {
    AiStreamChunk chunk = AiStreamChunk.builder()
        .type(StreamPayloadType.TEXT)
        .sessionId(sessionId)
        .messageId(messageId)
        .content(content)
        .completed(false)
        .timestamp(Instant.now())
        .build();
    messagingTemplate.convertAndSend(topic(sessionId), chunk);
    metricsService.onChunkSent();
  }

  public void publishError(Long sessionId, Long messageId, String content) {
    AiStreamChunk error = AiStreamChunk.builder()
        .type(StreamPayloadType.ERROR)
        .sessionId(sessionId)
        .messageId(messageId)
        .content(content)
        .completed(false)
        .timestamp(Instant.now())
        .build();
    messagingTemplate.convertAndSend(topic(sessionId), error);
    metricsService.onChunkSent();
  }

  public void publishComplete(Long sessionId, Long messageId) {
    AiStreamComplete complete = AiStreamComplete.builder()
        .type(StreamPayloadType.COMPLETE)
        .sessionId(sessionId)
        .messageId(messageId)
        .completed(true)
        .timestamp(Instant.now())
        .build();
    messagingTemplate.convertAndSend(topic(sessionId), complete);
  }

  private String topic(Long sessionId) {
    return "/topic/ai/" + sessionId;
  }
}
