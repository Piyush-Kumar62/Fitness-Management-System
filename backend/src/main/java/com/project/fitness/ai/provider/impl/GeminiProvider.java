package com.project.fitness.ai.provider.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.fitness.ai.config.AiProperties;
import com.project.fitness.ai.exception.AiProviderException;
import com.project.fitness.ai.provider.AiCapability;
import com.project.fitness.ai.provider.AiProvider;
import com.project.fitness.ai.provider.AiProviderType;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class GeminiProvider implements AiProvider {

  private final WebClient webClient;
  private final AiProperties properties;
  private final ObjectMapper objectMapper;

  public GeminiProvider(WebClient.Builder builder, AiProperties properties, ObjectMapper objectMapper) {
    this.properties = properties;
    this.objectMapper = objectMapper;
    this.webClient = builder.baseUrl(properties.getGemini().getBaseUrl()).build();
  }

  @Override
  public AiProviderType getType() {
    return AiProviderType.GEMINI;
  }

  @Override
  public String getModel() {
    return properties.getGemini().getModel();
  }

  @Override
  public boolean supports(AiCapability capability) {
    return capability == AiCapability.CHAT;
  }

  @Override
  public boolean isHealthy() {
    String apiKey = properties.getGemini().getApiKey();
    return apiKey != null && !apiKey.isBlank();
  }

  @Override
  public String chat(String prompt) {
    String apiKey = properties.getGemini().getApiKey();
    if (apiKey == null || apiKey.isBlank()) {
      throw new AiProviderException("Gemini API key is not configured");
    }

    String uri = "/v1beta/models/" + getModel() + ":generateContent?key=" + apiKey;
    Map<String, Object> payload = Map.of(
        "contents",
        List.of(Map.of("role", "user", "parts", List.of(Map.of("text", prompt)))));

    try {
      String response = webClient.post()
          .uri(uri)
          .bodyValue(payload)
          .retrieve()
          .bodyToMono(String.class)
          .block();
      return extractGeminiText(response);
    } catch (Exception ex) {
      throw new AiProviderException("Gemini request failed", ex);
    }
  }

  private String extractGeminiText(String response) {
    if (response == null || response.isBlank()) {
      throw new AiProviderException("Gemini response was empty");
    }
    try {
      JsonNode root = objectMapper.readTree(response);
      JsonNode textNode = root.path("candidates")
          .path(0)
          .path("content")
          .path("parts")
          .path(0)
          .path("text");
      if (textNode.isMissingNode()) {
        throw new AiProviderException("Gemini response did not include text");
      }
      return textNode.asText();
    } catch (Exception ex) {
      throw new AiProviderException("Failed to parse Gemini response", ex);
    }
  }
}
