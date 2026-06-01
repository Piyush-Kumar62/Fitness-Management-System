package com.project.fitness.ai.provider.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.fitness.ai.config.AiProperties;
import com.project.fitness.ai.exception.AiProviderException;
import com.project.fitness.ai.provider.AiCapability;
import com.project.fitness.ai.provider.AiProvider;
import com.project.fitness.ai.provider.AiProviderType;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class OllamaProvider implements AiProvider {

  private final WebClient webClient;
  private final AiProperties properties;
  private final ObjectMapper objectMapper;

  public OllamaProvider(WebClient.Builder builder, AiProperties properties, ObjectMapper objectMapper) {
    this.properties = properties;
    this.objectMapper = objectMapper;
    this.webClient = builder.baseUrl(properties.getOllama().getBaseUrl()).build();
  }

  @Override
  public AiProviderType getType() {
    return AiProviderType.OLLAMA;
  }

  @Override
  public String getModel() {
    return properties.getOllama().getModel();
  }

  @Override
  public boolean supports(AiCapability capability) {
    return capability == AiCapability.CHAT;
  }

  @Override
  public boolean isHealthy() {
    try {
      String response = webClient.get()
          .uri("/api/tags")
          .retrieve()
          .bodyToMono(String.class)
          .block();
      return response != null && !response.isBlank();
    } catch (Exception ex) {
      return false;
    }
  }

  @Override
  public String chat(String prompt) {
    Map<String, Object> payload = Map.of(
        "model", getModel(),
        "prompt", prompt,
        "stream", false);

    try {
      String response = webClient.post()
          .uri("/api/generate")
          .bodyValue(payload)
          .retrieve()
          .bodyToMono(String.class)
          .block();
      return extractOllamaText(response);
    } catch (Exception ex) {
      throw new AiProviderException("Ollama request failed", ex);
    }
  }

  private String extractOllamaText(String response) {
    if (response == null || response.isBlank()) {
      throw new AiProviderException("Ollama response was empty");
    }
    try {
      JsonNode root = objectMapper.readTree(response);
      JsonNode textNode = root.path("response");
      if (textNode.isMissingNode()) {
        throw new AiProviderException("Ollama response did not include text");
      }
      return textNode.asText();
    } catch (Exception ex) {
      throw new AiProviderException("Failed to parse Ollama response", ex);
    }
  }
}
