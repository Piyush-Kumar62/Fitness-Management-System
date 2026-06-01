package com.project.fitness.ai.tool.intent;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.fitness.ai.service.port.ProviderPort;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class IntentResolver {

  private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};
  private static final String SYSTEM_PROMPT = String.join("\n",
      "You are an intent classifier for a fitness assistant.",
      "Return JSON only with a single key: intent.",
      "Valid intents: membership_lookup, attendance_lookup, trainer_lookup, workout_generation, general_chat.",
      "If uncertain, choose general_chat.");

  private final ProviderPort providerPort;
  private final ObjectMapper objectMapper;

  public IntentResolver(ProviderPort providerPort, ObjectMapper objectMapper) {
    this.providerPort = providerPort;
    this.objectMapper = objectMapper;
  }

  public AiIntentResult resolve(String prompt) {
    String request = SYSTEM_PROMPT + "\nUser: " + prompt;
    ProviderPort.ProviderResult result = providerPort.chatWithFallback(request);
    String raw = result.reply() == null ? "" : result.reply().trim();
    AiIntentType intent = parseIntent(raw, prompt);
    return new AiIntentResult(intent, raw);
  }

  private AiIntentType parseIntent(String response, String prompt) {
    String json = extractJson(response);
    if (json != null) {
      try {
        Map<String, Object> parsed = objectMapper.readValue(json, MAP_TYPE);
        Object value = parsed.get("intent");
        return AiIntentType.fromValue(value == null ? null : value.toString());
      } catch (Exception ignored) {
        // Fall through to heuristic parsing.
      }
    }
    return heuristicIntent(prompt);
  }

  private String extractJson(String response) {
    int start = response.indexOf('{');
    int end = response.lastIndexOf('}');
    if (start >= 0 && end > start) {
      return response.substring(start, end + 1);
    }
    return null;
  }

  private AiIntentType heuristicIntent(String prompt) {
    if (prompt == null) {
      return AiIntentType.GENERAL_CHAT;
    }
    String normalized = prompt.toLowerCase(Locale.ROOT);
    if (normalized.contains("membership") || normalized.contains("plan") && normalized.contains("active")) {
      return AiIntentType.MEMBERSHIP_LOOKUP;
    }
    if (normalized.contains("attendance") || normalized.contains("check-in")) {
      return AiIntentType.ATTENDANCE_LOOKUP;
    }
    if (normalized.contains("trainer") || normalized.contains("coach")) {
      return AiIntentType.TRAINER_LOOKUP;
    }
    if (normalized.contains("workout") || normalized.contains("plan") || normalized.contains("routine")) {
      return AiIntentType.WORKOUT_GENERATION;
    }
    return AiIntentType.GENERAL_CHAT;
  }
}
