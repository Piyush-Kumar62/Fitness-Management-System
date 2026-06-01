package com.project.fitness.ai.provider;

import com.project.fitness.ai.config.AiProperties;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class AiProviderRegistry {

  private final Map<AiProviderType, AiProvider> providers;
  private final AiProperties properties;

  public AiProviderRegistry(List<AiProvider> providers, AiProperties properties) {
    this.properties = properties;
    this.providers = new EnumMap<>(AiProviderType.class);
    for (AiProvider provider : providers) {
      this.providers.put(provider.getType(), provider);
    }
  }

  public List<AiProvider> getOrderedProviders() {
    List<AiProvider> ordered = new ArrayList<>();
    for (AiProviderType type : properties.getProviderOrder()) {
      AiProvider provider = providers.get(type);
      if (provider != null) {
        ordered.add(provider);
      }
    }
    if (!ordered.isEmpty()) {
      return ordered;
    }
    return new ArrayList<>(providers.values());
  }
}
