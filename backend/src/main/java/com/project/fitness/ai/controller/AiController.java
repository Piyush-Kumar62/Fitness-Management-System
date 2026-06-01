package com.project.fitness.ai.controller;

import com.project.fitness.ai.dto.AiChatRequest;
import com.project.fitness.ai.dto.AiChatResponse;
import com.project.fitness.ai.service.AiChatService;
import com.project.fitness.common.exception.UnauthorizedException;
import com.project.fitness.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
public class AiController {

  private final AiChatService aiChatService;
  private final com.project.fitness.ai.service.AiProviderHealthService healthService;

  public AiController(AiChatService aiChatService,
      com.project.fitness.ai.service.AiProviderHealthService healthService) {
    this.aiChatService = aiChatService;
    this.healthService = healthService;
  }

  @PostMapping("/chat")
  public ResponseEntity<ApiResponse<AiChatResponse>> chat(
      Authentication authentication,
      @Valid @RequestBody AiChatRequest request) {
    if (authentication == null || authentication.getPrincipal() == null) {
      throw new UnauthorizedException("Authentication is required");
    }
    String userId = (String) authentication.getPrincipal();
    AiChatResponse response = aiChatService.chat(request, userId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @org.springframework.web.bind.annotation.GetMapping("/health")
  public ResponseEntity<ApiResponse<java.util.Map<String, String>>> health() {
    return ResponseEntity.ok(ApiResponse.success(healthService.checkProviders()));
  }
}
