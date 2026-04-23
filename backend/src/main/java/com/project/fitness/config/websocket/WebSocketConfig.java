package com.project.fitness.config.websocket;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

  @Value("${app.cors.allowed-origins:http://localhost:4200}")
  private String allowedOrigins;

  private final NotificationWebSocketHandler notificationWebSocketHandler;
  private final UserHandshakeInterceptor userHandshakeInterceptor;

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    String[] origins = resolveAllowedOrigins();

    registry.addHandler(notificationWebSocketHandler, "/ws/notifications")
        .addInterceptors(userHandshakeInterceptor)
        .setAllowedOrigins(origins);

    // SockJS compatibility endpoint to prevent /ws/info 404 noise
    // from clients/proxies that probe SockJS transport.
    registry.addHandler(notificationWebSocketHandler, "/ws")
        .addInterceptors(userHandshakeInterceptor)
        .setAllowedOrigins(origins)
        .withSockJS();
  }

  private String[] resolveAllowedOrigins() {
    List<String> origins = Arrays.stream(allowedOrigins.split(","))
        .map(String::trim)
        .filter(value -> !value.isBlank())
        .collect(Collectors.toList());
    if (origins.isEmpty()) {
      return new String[] {"http://localhost:4200"};
    }
    return origins.toArray(String[]::new);
  }
}
