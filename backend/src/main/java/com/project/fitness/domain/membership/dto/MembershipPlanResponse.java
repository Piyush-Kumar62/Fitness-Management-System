package com.project.fitness.domain.membership.dto;
import com.project.fitness.domain.membership.model.Membership;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipPlanResponse {

  private String id;
  private String gymId;
  private String name;
  private BigDecimal price;
  private Integer durationDays;
  private String features;
  private boolean active;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
