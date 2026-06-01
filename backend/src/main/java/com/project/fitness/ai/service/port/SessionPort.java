package com.project.fitness.ai.service.port;

public interface SessionPort {
  String ensureSession(String sessionId, String userId, String title);
}
