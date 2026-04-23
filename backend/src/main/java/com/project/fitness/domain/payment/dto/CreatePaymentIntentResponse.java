package com.project.fitness.domain.payment.dto;
import com.project.fitness.domain.payment.model.Payment;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Response DTO returned after creating a Stripe PaymentIntent. The frontend uses the clientSecret to confirm the payment with Stripe.js.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentIntentResponse {

    // The Stripe client secret – passed to stripe.confirmCardPayment() on the frontend.
    private String clientSecret;

    // Stripe PaymentIntent ID (pi_xxx) for reference/logging.
    private String paymentIntentId;

    // Amount in major currency unit (e.g. 999.00 INR).
    private BigDecimal amount;

    // Currency code (e.g. "INR").
    private String currency;

    // Human-readable plan name to display in checkout UI.
    private String planName;

    // Plan ID for reference.
    private String planId;
}
