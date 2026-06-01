package com.project.fitness.ai.controller;

import com.project.fitness.ai.chat.dto.ChatMessagePairResponse;
import com.project.fitness.ai.chat.dto.ChatMessageRequest;
import com.project.fitness.ai.chat.dto.ChatMessageResponse;
import com.project.fitness.ai.chat.dto.ChatSessionCreateRequest;
import com.project.fitness.ai.chat.dto.ChatSessionResponse;
import com.project.fitness.ai.chat.dto.ChatSessionUpdateRequest;
import com.project.fitness.ai.prompt.AiRoleResolver;
import com.project.fitness.ai.service.AiMessageApplicationService;
import com.project.fitness.ai.service.AiSessionApplicationService;
import com.project.fitness.common.exception.UnauthorizedException;
import com.project.fitness.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
public class AiChatSessionController {

  private final AiSessionApplicationService sessionApplicationService;
  private final AiMessageApplicationService messageApplicationService;
  private final AiRoleResolver roleResolver;

  public AiChatSessionController(
      AiSessionApplicationService sessionApplicationService,
      AiMessageApplicationService messageApplicationService,
      AiRoleResolver roleResolver) {
    this.sessionApplicationService = sessionApplicationService;
    this.messageApplicationService = messageApplicationService;
    this.roleResolver = roleResolver;
  }

  @PostMapping("/sessions")
  public ResponseEntity<ApiResponse<ChatSessionResponse>> createSession(
      Authentication authentication,
      @Valid @RequestBody ChatSessionCreateRequest request) {
    String userId = requireUser(authentication);
    ChatSessionResponse response = sessionApplicationService.create(
        userId,
        roleResolver.resolve(authentication),
        request.getTitle());
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping("/sessions")
  public ResponseEntity<ApiResponse<Page<ChatSessionResponse>>> listSessions(
      Authentication authentication,
      Pageable pageable) {
    String userId = requireUser(authentication);
    Page<ChatSessionResponse> response = sessionApplicationService.list(userId, pageable);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping("/sessions/{id}")
  public ResponseEntity<ApiResponse<ChatSessionResponse>> getSession(
      Authentication authentication,
      @PathVariable("id") Long sessionId) {
    String userId = requireUser(authentication);
    ChatSessionResponse response = sessionApplicationService.get(userId, sessionId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PatchMapping("/sessions/{id}")
  public ResponseEntity<ApiResponse<ChatSessionResponse>> renameSession(
      Authentication authentication,
      @PathVariable("id") Long sessionId,
      @Valid @RequestBody ChatSessionUpdateRequest request) {
    String userId = requireUser(authentication);
    ChatSessionResponse response = sessionApplicationService.rename(userId, sessionId, request.getTitle());
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @DeleteMapping("/sessions/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteSession(
      Authentication authentication,
      @PathVariable("id") Long sessionId) {
    String userId = requireUser(authentication);
    sessionApplicationService.delete(userId, sessionId);
    return ResponseEntity.ok(ApiResponse.success(null, "Session deleted"));
  }

  @PostMapping("/sessions/{id}/messages")
  public ResponseEntity<ApiResponse<ChatMessagePairResponse>> sendMessage(
      Authentication authentication,
      @PathVariable("id") Long sessionId,
      @Valid @RequestBody ChatMessageRequest request) {
    String userId = requireUser(authentication);
    ChatMessagePairResponse response = messageApplicationService.sendMessage(userId, sessionId, request.getContent());
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping("/sessions/{id}/messages")
  public ResponseEntity<ApiResponse<Page<ChatMessageResponse>>> listMessages(
      Authentication authentication,
      @PathVariable("id") Long sessionId,
      Pageable pageable) {
    String userId = requireUser(authentication);
    Page<ChatMessageResponse> response = messageApplicationService.listMessages(userId, sessionId, pageable);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  private String requireUser(Authentication authentication) {
    if (authentication == null || authentication.getPrincipal() == null) {
      throw new UnauthorizedException("Authentication is required");
    }
    return (String) authentication.getPrincipal();
  }
}
