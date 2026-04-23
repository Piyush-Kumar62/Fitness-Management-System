package com.project.fitness.domain.payment.controller;
import com.project.fitness.modules.payment.application.PaymentApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Stripe Webhook endpoint. This endpoint must be PUBLICLY accessible (no JWT auth) and should be excluded from CSRF protection. Stripe sends events here directly. Configure in your Stripe dashboard: Endpoint URL: https://your-domain.com/api/stripe/webhook Events to listen: payment_intent.succeeded, payment_intent.payment_failed
@RestController
@RequestMapping("/api/v1/stripe")
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookController {

    private final PaymentApplicationService paymentApplicationService;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "Stripe-Signature", required = false) String sigHeader) {

        try {
            paymentApplicationService.handleWebhookEvent(payload, sigHeader);
            return ResponseEntity.ok("Webhook processed");
        } catch (Exception e) {
            log.error("Webhook processing error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook error: " + e.getMessage());
        }
    }
}
