package com.project.fitness.domain.notification.service;

import com.project.fitness.config.websocket.NotificationWebSocketHandler;
import com.project.fitness.domain.notification.dto.NotificationMessage;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final NotificationWebSocketHandler notificationWebSocketHandler;

  public void notifyUser(String userId, String type, String title, String message) {
    NotificationMessage payload = NotificationMessage.builder()
        .type(type)
        .title(title)
        .message(message)
        .timestamp(LocalDateTime.now())
        .build();
    notificationWebSocketHandler.sendToUser(userId, payload);
  }
}
