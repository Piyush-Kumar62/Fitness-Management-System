package com.project.fitness.ai.service;

import org.springframework.stereotype.Service;

@Service
public class NoopRateLimitService implements RateLimitService {

  @Override
  public void checkOrThrow(String userId, String action) {
    // Extension point for future rate limiting.
  }
}
