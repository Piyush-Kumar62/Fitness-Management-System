package com.project.fitness.domain.notification.service;
import com.project.fitness.domain.user.model.User;

import com.project.fitness.domain.notification.dto.NotificationMessage;

// Contract for real-time WebSocket notifications.
public interface INotificationService {
  // Send a notification to a specific user by their userId.
  void sendToUser(String userId, NotificationMessage message);
}
