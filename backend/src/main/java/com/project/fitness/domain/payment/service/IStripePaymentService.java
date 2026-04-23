package com.project.fitness.domain.payment.service;
import com.project.fitness.domain.payment.model.Payment;

import com.project.fitness.domain.payment.dto.CreatePaymentIntentRequest;
import com.project.fitness.domain.payment.dto.CreatePaymentIntentResponse;
import com.stripe.exception.StripeException;

// Contract for Stripe payment operations.
public interface IStripePaymentService {
  CreatePaymentIntentResponse createPaymentIntent(String userId, CreatePaymentIntentRequest req)
      throws StripeException;
  void handleWebhookEvent(String payload, String sigHeader);
}
