package com.project.fitness.ai.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatMessage {

  private Role role;
  private String content;
  private Instant timestamp;

  public enum Role {
    SYSTEM,
    USER,
    ASSISTANT
  }
}
