package com.project.fitness.ai.tool.impl;

import com.project.fitness.ai.tool.AiTool;
import com.project.fitness.ai.tool.ToolExecutionContext;
import com.project.fitness.ai.tool.ToolResult;
import com.project.fitness.domain.fitness.service.WorkoutService;
import com.project.fitness.domain.user.model.UserRole;
import com.project.fitness.domain.user.service.UserService;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class WorkoutTool implements AiTool {

  private final WorkoutService workoutService;
  private final UserService userService;

  public WorkoutTool(WorkoutService workoutService, UserService userService) {
    this.workoutService = workoutService;
    this.userService = userService;
  }

  @Override
  public String name() {
    return "WorkoutTool";
  }

  @Override
  public ToolResult execute(ToolExecutionContext context) {
    UserRole role = userService.getUserById(context.userId()).getRole();
    Map<String, Object> plan = workoutService.generatePlan(context.prompt(), role);
    return new ToolResult(true, name(), "Workout plan generated", Map.of("plan", plan));
  }
}
