package com.project.fitness.ai.tool;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class AiToolRegistry {

  private final Map<String, AiTool> tools;

  public AiToolRegistry(List<AiTool> tools) {
    this.tools = tools.stream().collect(Collectors.toMap(AiTool::name, tool -> tool));
  }

  public Map<String, AiTool> getTools() {
    return tools;
  }
}
