package com.project.fitness.ai.service;

import com.project.fitness.ai.config.AiProperties;
import org.springframework.stereotype.Service;

@Service
public class AiFeatureFlagService {

  private final AiProperties properties;

  public AiFeatureFlagService(AiProperties properties) {
    this.properties = properties;
  }

  public boolean isChatEnabled() {
    return properties.getFeatures().isChatbotEnabled();
  }

  public boolean isToolCallingEnabled() {
    return properties.getFeatures().isToolCallingEnabled();
  }

  public boolean isVoiceEnabled() {
    return false;
  }

  public boolean isRagEnabled() {
    return properties.getFeatures().isRagEnabled();
  }
}
