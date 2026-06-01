package com.project.fitness.ai.websocket.config;

import com.project.fitness.ai.prompt.AiRoleResolver;
import com.project.fitness.ai.websocket.service.AiStreamingMetricsService;
import com.project.fitness.ai.chat.service.ChatSessionService;
import com.project.fitness.domain.user.model.UserRole;
import com.project.fitness.security.JwtUtils;
import io.jsonwebtoken.Claims;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class AiWebSocketSecurityInterceptor implements ChannelInterceptor {

  private final JwtUtils jwtUtils;
  private final AiStreamingMetricsService metricsService;
  private final ChatSessionService chatSessionService;
  private final AiRoleResolver roleResolver;

  public AiWebSocketSecurityInterceptor(
      JwtUtils jwtUtils,
      AiStreamingMetricsService metricsService,
      ChatSessionService chatSessionService,
      AiRoleResolver roleResolver) {
    this.jwtUtils = jwtUtils;
    this.metricsService = metricsService;
    this.chatSessionService = chatSessionService;
    this.roleResolver = roleResolver;
  }

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
    if (accessor == null || accessor.getCommand() == null) {
      return message;
    }

    if (accessor.getCommand() == StompCommand.CONNECT) {
      String token = extractToken(accessor);
      if (token == null || !jwtUtils.validateJwtToken(token)) {
        return null;
      }
      String userId = jwtUtils.getUserIdFromToken(token);
      Collection<GrantedAuthority> authorities = resolveAuthorities(token);
      UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userId, null, authorities);
      accessor.setUser(auth);
    } else if (accessor.getCommand() == StompCommand.SEND) {
      Principal user = accessor.getUser();
      if (user == null) {
        return null;
      }
      metricsService.onMessageReceived();
    } else if (accessor.getCommand() == StompCommand.SUBSCRIBE) {
      Principal user = accessor.getUser();
      if (user == null) {
        return null;
      }
      if (!validateTopicSubscription(accessor, user)) {
        return null;
      }
    }

    return message;
  }

  private String extractToken(StompHeaderAccessor accessor) {
    String authHeader = accessor.getFirstNativeHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7);
    }
    String token = accessor.getFirstNativeHeader("token");
    return (token != null && !token.isBlank()) ? token : null;
  }

  private Collection<GrantedAuthority> resolveAuthorities(String token) {
    Claims claims = jwtUtils.getAllClaims(token);
    Object roles = claims.get("roles");
    if (roles instanceof List<?> list) {
      return list.stream()
          .map(Object::toString)
          .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
          .map(SimpleGrantedAuthority::new)
          .collect(Collectors.toList());
    }
    String role = String.valueOf(claims.get("role"));
    if (role != null && !role.equals("null")) {
      List<GrantedAuthority> authorities = new ArrayList<>();
      authorities.add(new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role));
      return authorities;
    }
    return List.of();
  }

  private boolean validateTopicSubscription(StompHeaderAccessor accessor, Principal user) {
    String destination = accessor.getDestination();
    if (destination == null || !destination.startsWith("/topic/ai/")) {
      return true;
    }
    String sessionIdValue = destination.substring("/topic/ai/".length());
    Long sessionId;
    try {
      sessionId = Long.parseLong(sessionIdValue);
    } catch (NumberFormatException ex) {
      return false;
    }
    String userId = user.getName();
    org.springframework.security.core.Authentication auth = accessor.getUser() instanceof org.springframework.security.core.Authentication
      ? (org.springframework.security.core.Authentication) accessor.getUser()
      : null;
    UserRole role = roleResolver.resolve(auth);
    try {
      var session = chatSessionService.get(userId, sessionId);
      return role == session.getRole() || role == UserRole.ADMIN;
    } catch (RuntimeException ex) {
      return false;
    }
  }
}
