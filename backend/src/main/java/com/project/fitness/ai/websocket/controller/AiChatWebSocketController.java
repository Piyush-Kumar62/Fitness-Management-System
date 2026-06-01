package com.project.fitness.ai.websocket.controller;

import com.project.fitness.ai.prompt.AiRoleResolver;
import com.project.fitness.ai.websocket.dto.AiStreamRequest;
import com.project.fitness.ai.websocket.service.AiStreamingApplicationService;
import com.project.fitness.common.exception.UnauthorizedException;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
public class AiChatWebSocketController {

  private final AiStreamingApplicationService streamingApplicationService;
  private final AiRoleResolver roleResolver;

  public AiChatWebSocketController(
      AiStreamingApplicationService streamingApplicationService,
      AiRoleResolver roleResolver) {
    this.streamingApplicationService = streamingApplicationService;
    this.roleResolver = roleResolver;
  }

  @MessageMapping("/ai/chat")
  public void chat(@Valid AiStreamRequest request, Authentication authentication) {
    if (authentication == null || authentication.getPrincipal() == null) {
      throw new UnauthorizedException("Authentication is required");
    }
    String userId = (String) authentication.getPrincipal();
    streamingApplicationService.stream(request, userId, roleResolver.resolve(authentication));
  }
}
