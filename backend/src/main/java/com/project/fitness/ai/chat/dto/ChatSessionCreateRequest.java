package com.project.fitness.ai.chat.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSessionCreateRequest {

  @Size(max = 255, message = "Title is too long")
  private String title;
}
