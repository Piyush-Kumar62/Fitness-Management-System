package com.project.fitness.ai.websocket.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiStreamChunk {

  private StreamPayloadType type;
  private Long sessionId;
  private Long messageId;
  private String content;
  private boolean completed;
  private Instant timestamp;
}
