package com.project.fitness.config.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.fitness.domain.notification.dto.NotificationMessage;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
public class NotificationWebSocketHandler extends TextWebSocketHandler {

  private final ObjectMapper objectMapper;
  private final ConcurrentHashMap<String, Set<WebSocketSession>> sessionsByUser = new ConcurrentHashMap<>();

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    String userId = (String) session.getAttributes().get("userId");
    if (userId == null || userId.isBlank()) {
      return;
    }
    sessionsByUser.computeIfAbsent(userId, key -> ConcurrentHashMap.newKeySet()).add(session);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    String userId = (String) session.getAttributes().get("userId");
    if (userId == null || !sessionsByUser.containsKey(userId)) {
      return;
    }
    sessionsByUser.get(userId).remove(session);
    if (sessionsByUser.get(userId).isEmpty()) {
      sessionsByUser.remove(userId);
    }
  }

  public void sendToUser(String userId, NotificationMessage payload) {
    Set<WebSocketSession> sessions = sessionsByUser.get(userId);
    if (sessions == null || sessions.isEmpty()) {
      return;
    }
    String json = toJson(payload);
    sessions.stream().filter(WebSocketSession::isOpen).forEach(session -> send(session, json));
  }

  private String toJson(NotificationMessage payload) {
    try {
      return objectMapper.writeValueAsString(payload);
    } catch (IOException e) {
      return "{\"type\":\"SYSTEM\",\"title\":\"Notification\",\"message\":\"Serialization error\"}";
    }
  }

  private void send(WebSocketSession session, String payload) {
    try {
      session.sendMessage(new TextMessage(payload));
    } catch (IOException ignored) {
      // best-effort notification delivery
    }
  }
}
