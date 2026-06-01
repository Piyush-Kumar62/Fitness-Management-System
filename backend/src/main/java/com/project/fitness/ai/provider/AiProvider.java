package com.project.fitness.ai.provider;

import com.project.fitness.ai.provider.AiCapability;

public interface AiProvider {
  AiProviderType getType();

  String getModel();

  boolean supports(AiCapability capability);

  String chat(String prompt);

  boolean isHealthy();
}
  