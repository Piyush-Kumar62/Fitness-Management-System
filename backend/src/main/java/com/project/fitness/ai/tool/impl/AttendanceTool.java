package com.project.fitness.ai.tool.impl;

import com.project.fitness.ai.tool.AiTool;
import com.project.fitness.ai.tool.ToolExecutionContext;
import com.project.fitness.ai.tool.ToolResult;
import com.project.fitness.domain.trainer.dto.AttendanceResponse;
import com.project.fitness.domain.trainer.service.ClassManagementService;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class AttendanceTool implements AiTool {

  private final ClassManagementService classManagementService;

  public AttendanceTool(ClassManagementService classManagementService) {
    this.classManagementService = classManagementService;
  }

  @Override
  public String name() {
    return "AttendanceTool";
  }

  @Override
  public ToolResult execute(ToolExecutionContext context) {
    LocalDate today = LocalDate.now();
    LocalDate start = today.withDayOfMonth(1);
    LocalDate end = today.withDayOfMonth(today.lengthOfMonth());
    List<AttendanceResponse> attendance = classManagementService.getMemberAttendance(
        context.userId(), start, end);

    Map<String, Object> data = new LinkedHashMap<>();
    data.put("month", today.getMonth().toString());
    data.put("year", today.getYear());
    data.put("total", attendance.size());
    data.put("items", attendance);
    return new ToolResult(true, name(), "Attendance summary retrieved", data);
  }
}
