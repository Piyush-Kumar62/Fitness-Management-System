package com.project.fitness.ai.service;

import org.springframework.stereotype.Component;

@Component
public class TitleSanitizer {

  private static final int MAX_LENGTH = 80;

  public String sanitize(String title) {
    if (title == null) {
      return "New Chat";
    }
    String trimmed = title.trim();
    if (trimmed.isEmpty()) {
      return "New Chat";
    }
    trimmed = removeSurroundingQuotes(trimmed);
    trimmed = removeTrailingPunctuation(trimmed).trim();
    if (trimmed.isEmpty()) {
      return "New Chat";
    }
    if (trimmed.length() > MAX_LENGTH) {
      trimmed = trimmed.substring(0, MAX_LENGTH).trim();
    }
    return trimmed.isEmpty() ? "New Chat" : trimmed;
  }

  private String removeSurroundingQuotes(String value) {
    if (value.length() < 2) {
      return value;
    }
    char first = value.charAt(0);
    char last = value.charAt(value.length() - 1);
    if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
      return value.substring(1, value.length() - 1).trim();
    }
    return value;
  }

  private String removeTrailingPunctuation(String value) {
    int end = value.length();
    while (end > 0) {
      char ch = value.charAt(end - 1);
      if (ch == '.' || ch == '!' || ch == '?' || ch == ',' || ch == ';' || ch == ':') {
        end--;
      } else {
        break;
      }
    }
    return value.substring(0, end);
  }
}
