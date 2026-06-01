package com.project.fitness.ai.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AiStructuredLogger {

  public void logRequest(String userId, Long sessionId, String provider) {
    log.info("event=ai_request userId={} sessionId={} provider={}", userId, sessionId, provider);
  }

  public void logResponse(String userId, Long sessionId, String provider, long latencyMs) {
    log.info("event=ai_response userId={} sessionId={} provider={} latencyMs={}",
        userId, sessionId, provider, latencyMs);
  }

  public void logToolSelected(String userId, String sessionId, String tool, String intent) {
    log.info("event=ai_tool_selected userId={} sessionId={} tool={} intent={}"
        , userId, sessionId, tool, intent);
  }

  public void logToolStarted(String userId, String sessionId, String tool) {
    log.info("event=ai_tool_started userId={} sessionId={} tool={}"
        , userId, sessionId, tool);
  }

  public void logToolCompleted(String userId, String sessionId, String tool, boolean success) {
    log.info("event=ai_tool_completed userId={} sessionId={} tool={} success={}"
        , userId, sessionId, tool, success);
  }

  public void logToolFailed(String userId, String sessionId, String tool, String error) {
    log.info("event=ai_tool_failed userId={} sessionId={} tool={} error={}"
        , userId, sessionId, tool, error);
  }
}
