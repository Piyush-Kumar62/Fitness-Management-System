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
public class AiChatResponse {

  private String reply;
  private String provider;
  private String model;
  private String source;
  private String sessionId;
  private Instant timestamp;
}
