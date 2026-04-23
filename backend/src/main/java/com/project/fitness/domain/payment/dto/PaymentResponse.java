package com.project.fitness.domain.payment.dto;
import com.project.fitness.domain.payment.model.Payment;

import com.project.fitness.domain.payment.model.PaymentMethod;
import com.project.fitness.domain.payment.model.PaymentStatus;
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
public class PaymentResponse {

  private String id;
  private String memberId;
  private String membershipId;
  private BigDecimal amount;
  private PaymentMethod method;
  private PaymentStatus status;
  private String transactionId;
  private String gateway;
  private LocalDateTime createdAt;
}
