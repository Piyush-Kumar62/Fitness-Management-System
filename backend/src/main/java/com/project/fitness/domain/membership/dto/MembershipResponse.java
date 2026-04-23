package com.project.fitness.domain.membership.dto;
import com.project.fitness.domain.membership.model.Membership;

import com.project.fitness.domain.membership.model.MembershipStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipResponse {

  private String id;
  private String memberId;
  private String memberName;
  private String planId;
  private String planName;
  private LocalDate startDate;
  private LocalDate endDate;
  private MembershipStatus status;
  private boolean autoRenew;
  private LocalDateTime createdAt;
}
