package com.project.fitness.ai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatRequest {

  @NotBlank(message = "Prompt is required")
  @Size(max = 4000, message = "Prompt is too long")
  private String prompt;

  private String sessionId;
}
