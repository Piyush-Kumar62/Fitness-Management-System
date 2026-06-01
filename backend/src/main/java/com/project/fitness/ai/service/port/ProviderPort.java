package com.project.fitness.ai.service.port;

import com.project.fitness.ai.provider.AiProviderType;

public interface ProviderPort {
  ProviderResult chatWithFallback(String prompt);

  record ProviderResult(AiProviderType providerType, String model, String reply, String source) {}
}
