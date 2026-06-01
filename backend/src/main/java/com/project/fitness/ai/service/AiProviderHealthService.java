package com.project.fitness.ai.service;

import com.project.fitness.ai.provider.AiProvider;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class AiProviderHealthService {

  private final List<AiProvider> providers;

  public AiProviderHealthService(List<AiProvider> providers) {
    this.providers = providers;
  }

  public Map<String, String> checkProviders() {
    Map<String, String> result = new LinkedHashMap<>();
    for (AiProvider provider : providers) {
      result.put(provider.getType().name().toLowerCase(), provider.isHealthy() ? "UP" : "DOWN");
    }
    return result;
  }
}
