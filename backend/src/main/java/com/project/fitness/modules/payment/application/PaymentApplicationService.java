package com.project.fitness.modules.payment.application;

import com.project.fitness.domain.payment.dto.CreatePaymentIntentRequest;
import com.project.fitness.domain.payment.dto.CreatePaymentIntentResponse;
import com.project.fitness.domain.payment.service.StripePaymentService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentApplicationService {

  private final StripePaymentService stripePaymentService;

  public CreatePaymentIntentResponse createPaymentIntent(String userId, CreatePaymentIntentRequest request)
      throws StripeException {
    return stripePaymentService.createPaymentIntent(userId, request);
  }

  public void handleWebhookEvent(String payload, String sigHeader) {
    stripePaymentService.handleWebhookEvent(payload, sigHeader);
  }
}
