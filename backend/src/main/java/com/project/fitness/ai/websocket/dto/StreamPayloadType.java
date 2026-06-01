package com.project.fitness.ai.websocket.dto;

public enum StreamPayloadType {
  TEXT,
  TOOL_CALL,
  TOOL_RESULT,
  ERROR,
  COMPLETE
}
