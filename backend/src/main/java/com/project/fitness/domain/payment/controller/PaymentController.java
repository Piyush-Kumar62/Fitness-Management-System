package com.project.fitness.domain.payment.controller;
import com.project.fitness.domain.payment.dto.CreatePaymentIntentRequest;
import com.project.fitness.domain.payment.dto.CreatePaymentIntentResponse;
import com.project.fitness.modules.payment.application.PaymentApplicationService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Exposes Stripe payment endpoints consumed by the Angular frontend. Flow: POST /api/payments/create-intent → returns { clientSecret, amount, ... } Frontend confirms via stripe.confirmCardPayment(clientSecret) Stripe webhook → POST /api/stripe/webhook (see StripeWebhookController)
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentApplicationService paymentApplicationService;

    // Create a Stripe PaymentIntent for a membership plan. Returns the client_secret needed by the frontend Stripe.js integration.
    @PostMapping("/create-intent")
    @PreAuthorize("hasAnyRole('MEMBER','OWNER','ADMIN')")
    public ResponseEntity<CreatePaymentIntentResponse> createPaymentIntent(
            Authentication authentication,
            @RequestBody CreatePaymentIntentRequest request) throws StripeException {

        String userId = (String) authentication.getPrincipal();
        CreatePaymentIntentResponse response = paymentApplicationService.createPaymentIntent(userId, request);
        return ResponseEntity.ok(response);
    }
}
