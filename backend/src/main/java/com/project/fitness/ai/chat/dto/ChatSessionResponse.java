package com.project.fitness.ai.chat.dto;

import com.project.fitness.ai.chat.entity.ChatSessionStatus;
import com.project.fitness.domain.user.model.UserRole;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSessionResponse {

  private Long id;
  private String title;
  private UserRole role;
  private ChatSessionStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
