package com.project.fitness.ai.websocket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiStreamRequest {

  @NotNull(message = "Session id is required")
  private Long sessionId;

  @NotBlank(message = "Message is required")
  @Size(max = 4000, message = "Message is too long")
  private String message;

  private Instant timestamp;
}
