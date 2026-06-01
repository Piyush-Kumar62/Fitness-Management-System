package com.project.fitness.domain.fitness.service;

import com.project.fitness.domain.user.model.UserRole;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class WorkoutService {

  public Map<String, Object> generatePlan(String prompt, UserRole role) {
    String normalized = prompt == null ? "" : prompt.toLowerCase(Locale.ROOT);
    String difficulty = resolveDifficulty(normalized);
    int durationWeeks = normalized.contains("4 week") ? 4 : normalized.contains("6 week") ? 6 : 4;
    String focus = resolveFocus(normalized);

    Map<String, Object> plan = new LinkedHashMap<>();
    plan.put("title", buildTitle(difficulty, focus));
    plan.put("difficulty", difficulty);
    plan.put("durationWeeks", durationWeeks);
    plan.put("focus", focus);
    plan.put("audience", role == null ? "member" : role.name().toLowerCase(Locale.ROOT));
    plan.put("schedule", buildSchedule(focus));
    plan.put("notes", "Start each session with 5-10 minutes of warm-up and finish with mobility work.");
    return plan;
  }

  private String resolveDifficulty(String prompt) {
    if (prompt.contains("advanced")) {
      return "ADVANCED";
    }
    if (prompt.contains("intermediate")) {
      return "INTERMEDIATE";
    }
    return "BEGINNER";
  }

  private String resolveFocus(String prompt) {
    if (prompt.contains("strength")) {
      return "Strength";
    }
    if (prompt.contains("weight loss") || prompt.contains("fat loss")) {
      return "Fat Loss";
    }
    if (prompt.contains("cardio") || prompt.contains("endurance")) {
      return "Endurance";
    }
    return "Balanced Fitness";
  }

  private String buildTitle(String difficulty, String focus) {
    String label = difficulty.substring(0, 1) + difficulty.substring(1).toLowerCase(Locale.ROOT);
    return label + " " + focus + " Plan";
  }

  private List<Map<String, Object>> buildSchedule(String focus) {
    List<Map<String, Object>> days = new ArrayList<>();
    days.add(day("Day 1", focus, "Full body strength circuit"));
    days.add(day("Day 2", focus, "Low-impact cardio + core"));
    days.add(day("Day 3", focus, "Lower body strength focus"));
    days.add(day("Day 4", focus, "Active recovery + mobility"));
    days.add(day("Day 5", focus, "Upper body strength focus"));
    days.add(day("Day 6", focus, "Interval cardio + conditioning"));
    days.add(day("Day 7", focus, "Rest and stretching"));
    return days;
  }

  private Map<String, Object> day(String label, String focus, String detail) {
    Map<String, Object> day = new LinkedHashMap<>();
    day.put("day", label);
    day.put("focus", focus);
    day.put("detail", detail);
    return day;
  }
}
