package com.project.fitness.domain.notification.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {

  private String type;
  private String title;
  private String message;
  private LocalDateTime timestamp;
}
