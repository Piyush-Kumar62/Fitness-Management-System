package com.project.fitness.ai.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessagePairResponse {

  private ChatMessageResponse userMessage;
  private ChatMessageResponse assistantMessage;
}
