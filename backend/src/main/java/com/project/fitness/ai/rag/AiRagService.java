package com.project.fitness.ai.rag;

import com.project.fitness.ai.config.AiProperties;
import org.springframework.stereotype.Service;

@Service
public class AiRagService {

  private final AiProperties properties;

  public AiRagService(AiProperties properties) {
    this.properties = properties;
  }

  public String enrichPrompt(String prompt) {
    if (!properties.getFeatures().isRagEnabled()) {
      return "";
    }
    return "";
  }
}
