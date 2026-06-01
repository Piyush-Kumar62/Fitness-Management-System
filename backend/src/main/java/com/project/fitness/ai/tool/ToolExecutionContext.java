package com.project.fitness.ai.tool;

import java.util.Collections;
import java.util.Map;

public record ToolExecutionContext(String userId, String sessionId, String prompt, Map<String, Object> attributes) {
  public ToolExecutionContext {
    attributes = attributes == null ? Collections.emptyMap() : Collections.unmodifiableMap(attributes);
  }

  public static ToolExecutionContext of(String userId, String sessionId, String prompt) {
    return new ToolExecutionContext(userId, sessionId, prompt, Collections.emptyMap());
  }
}
