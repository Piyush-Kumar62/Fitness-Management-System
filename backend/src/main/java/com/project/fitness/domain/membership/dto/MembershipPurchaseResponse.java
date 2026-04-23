package com.project.fitness.domain.membership.dto;
import com.project.fitness.domain.payment.dto.PaymentResponse;
import com.project.fitness.domain.payment.model.Payment;
import com.project.fitness.domain.membership.model.Membership;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipPurchaseResponse {

  private MembershipResponse membership;
  private PaymentResponse payment;
}
