package com.project.fitness.config.websocket;

import com.project.fitness.security.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Component
public class UserHandshakeInterceptor implements HandshakeInterceptor {

  private final JwtUtils jwtUtils;

  public UserHandshakeInterceptor(JwtUtils jwtUtils) {
    this.jwtUtils = jwtUtils;
  }

  @Override
  public boolean beforeHandshake(ServerHttpRequest request, org.springframework.http.server.ServerHttpResponse response,
      WebSocketHandler wsHandler, Map<String, Object> attributes) {
    if (request instanceof ServletServerHttpRequest servletRequest) {
      HttpServletRequest raw = servletRequest.getServletRequest();
      String token = extractToken(raw);
      if (token != null && jwtUtils.validateJwtToken(token)) {
        String userId = jwtUtils.getUserIdFromToken(token);
        if (userId != null && !userId.isBlank()) {
          attributes.put("userId", userId);
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public void afterHandshake(ServerHttpRequest request, org.springframework.http.server.ServerHttpResponse response,
      WebSocketHandler wsHandler, Exception exception) {
    // no-op
  }

  private String extractToken(HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7);
    }
    String tokenParam = request.getParameter("token");
    return (tokenParam != null && !tokenParam.isBlank()) ? tokenParam : null;
  }
}
