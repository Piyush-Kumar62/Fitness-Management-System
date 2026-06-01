package com.project.fitness.ai.chat.entity;

public enum ConversationEventType {
  MESSAGE_CREATED,
  STREAM_STARTED,
  STREAM_CHUNK,
  STREAM_COMPLETED,
  STREAM_FAILED,
  TOOL_SELECTED,
  TOOL_STARTED,
  TOOL_COMPLETED,
  TOOL_FAILED
}
