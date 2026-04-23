package com.project.fitness.domain.membership.dto;
import com.project.fitness.domain.payment.model.Payment;
import com.project.fitness.domain.membership.model.Membership;

import com.project.fitness.domain.payment.model.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyMembershipRequest {

  private String memberId;

  @NotBlank(message = "Plan ID is required")
  private String planId;

  @NotNull(message = "Payment method is required")
  private PaymentMethod paymentMethod;
}
