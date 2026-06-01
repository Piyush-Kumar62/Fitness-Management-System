package com.project.fitness.ai.websocket.config;

import com.project.fitness.ai.websocket.service.AiStreamingMetricsService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.SessionConnectEvent;
import org.springframework.messaging.simp.stomp.SessionDisconnectEvent;
import org.springframework.stereotype.Component;

@Component
public class AiWebSocketEventListener {

  private final AiStreamingMetricsService metricsService;

  public AiWebSocketEventListener(AiStreamingMetricsService metricsService) {
    this.metricsService = metricsService;
  }

  @EventListener
  public void handleConnect(SessionConnectEvent event) {
    metricsService.onConnect();
  }

  @EventListener
  public void handleDisconnect(SessionDisconnectEvent event) {
    metricsService.onDisconnect();
  }
}
