package com.project.fitness.ai.tool;

import java.util.Map;

public interface AiTool {
  String name();

  ToolResult execute(ToolExecutionContext context);
}
