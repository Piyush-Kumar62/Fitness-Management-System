package com.project.fitness.domain.payment.dto;
import com.project.fitness.domain.payment.model.Payment;
import com.project.fitness.domain.user.model.User;
import com.project.fitness.domain.membership.model.Membership;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Request DTO sent by the frontend to initiate a Stripe PaymentIntent.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentIntentRequest {

    // The membership plan ID the user wants to purchase.
    private String planId;

    // ISO 4217 currency code (e.g. "INR", "USD"). Defaults to "INR" if not provided.
    private String currency;
}
