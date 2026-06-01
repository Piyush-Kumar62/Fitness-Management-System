package com.project.fitness.ai.tool.intent;

public enum AiIntentType {
  MEMBERSHIP_LOOKUP("membership_lookup"),
  ATTENDANCE_LOOKUP("attendance_lookup"),
  TRAINER_LOOKUP("trainer_lookup"),
  WORKOUT_GENERATION("workout_generation"),
  GENERAL_CHAT("general_chat");

  private final String value;

  AiIntentType(String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }

  public static AiIntentType fromValue(String value) {
    if (value == null) {
      return GENERAL_CHAT;
    }
    String normalized = value.trim().toLowerCase();
    for (AiIntentType type : values()) {
      if (type.value.equals(normalized)) {
        return type;
      }
    }
    return GENERAL_CHAT;
  }
}
