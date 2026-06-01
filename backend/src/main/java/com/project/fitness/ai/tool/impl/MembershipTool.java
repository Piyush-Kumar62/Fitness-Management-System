package com.project.fitness.ai.tool.impl;

import com.project.fitness.ai.tool.AiTool;
import com.project.fitness.ai.tool.ToolExecutionContext;
import com.project.fitness.ai.tool.ToolResult;
import com.project.fitness.domain.membership.dto.MembershipResponse;
import com.project.fitness.domain.membership.service.MembershipService;
import com.project.fitness.domain.user.model.UserRole;
import com.project.fitness.domain.user.service.UserService;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class MembershipTool implements AiTool {

  private final MembershipService membershipService;
  private final UserService userService;

  public MembershipTool(MembershipService membershipService, UserService userService) {
    this.membershipService = membershipService;
    this.userService = userService;
  }

  @Override
  public String name() {
    return "MembershipTool";
  }

  @Override
  public ToolResult execute(ToolExecutionContext context) {
    var user = userService.getUserById(context.userId());
    if (user.getRole() != UserRole.MEMBER) {
      return new ToolResult(false, name(), "Only members can access membership details", Map.of());
    }
    MembershipResponse membership = membershipService.getActiveMembership(context.userId());
    if (membership == null) {
      return new ToolResult(false, name(), "No active membership found", Map.of("active", false));
    }

    Map<String, Object> data = new LinkedHashMap<>();
    data.put("active", true);
    data.put("plan", membership.getPlanName());
    data.put("status", membership.getStatus());
    data.put("startDate", membership.getStartDate());
    data.put("endDate", membership.getEndDate());
    data.put("autoRenew", membership.isAutoRenew());
    return new ToolResult(true, name(), "Active membership retrieved", data);
  }
}
