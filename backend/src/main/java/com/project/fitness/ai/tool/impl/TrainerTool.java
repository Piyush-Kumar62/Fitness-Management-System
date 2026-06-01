package com.project.fitness.ai.tool.impl;

import com.project.fitness.ai.tool.AiTool;
import com.project.fitness.ai.tool.ToolExecutionContext;
import com.project.fitness.ai.tool.ToolResult;
import com.project.fitness.domain.user.dto.UserResponse;
import com.project.fitness.domain.user.service.UserService;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class TrainerTool implements AiTool {

  private final UserService userService;

  public TrainerTool(UserService userService) {
    this.userService = userService;
  }

  @Override
  public String name() {
    return "TrainerTool";
  }

  @Override
  public ToolResult execute(ToolExecutionContext context) {
    UserResponse trainer = userService.getTrainerForMember(context.userId());
    if (trainer == null) {
      return new ToolResult(false, name(), "No trainer assigned", Map.of("assigned", false));
    }

    Map<String, Object> data = new LinkedHashMap<>();
    data.put("assigned", true);
    data.put("trainerId", trainer.getId());
    data.put("trainerName", trainer.getFirstName() + " " + trainer.getLastName());
    data.put("trainerEmail", trainer.getEmail());
    return new ToolResult(true, name(), "Trainer retrieved", data);
  }
}
