package com.project.fitness.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatCommand {

  private String prompt;
  private String sessionId;
  private String userId;
  private String systemPrompt;
  private String contextSummary;
  private Boolean persistMessages;
}
