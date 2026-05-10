package com.project.fitness.domain.notification.controller;

import com.project.fitness.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;

  @PostMapping("/send")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> sendNotification(
      @RequestParam String userId,
      @RequestParam String type,
      @RequestParam String title,
      @RequestParam String message) {
    notificationService.notifyUser(userId, type, title, message);
    return ResponseEntity.ok().build();
  }
}
