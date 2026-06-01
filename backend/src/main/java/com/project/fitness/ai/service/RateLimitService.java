package com.project.fitness.ai.service;

public interface RateLimitService {
  void checkOrThrow(String userId, String action);
}
