package com.project.fitness.ai.tool;

import java.util.Map;

public record ToolResult(boolean success, String tool, String message, Map<String, Object> data) {
}
