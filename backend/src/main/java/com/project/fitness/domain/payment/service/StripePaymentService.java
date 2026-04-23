package com.project.fitness.domain.payment.service;

import com.project.fitness.domain.membership.service.MembershipService;
import com.project.fitness.domain.membership.dto.BuyMembershipRequest;
import com.project.fitness.domain.notification.service.IEmailService;
import com.project.fitness.domain.membership.model.Membership;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import com.project.fitness.domain.payment.dto.CreatePaymentIntentRequest;
import com.project.fitness.domain.payment.dto.CreatePaymentIntentResponse;
import com.project.fitness.domain.membership.dto.MembershipPurchaseResponse;
import com.project.fitness.common.exception.BadRequestException;
import com.project.fitness.common.exception.ResourceNotFoundException;
import com.project.fitness.domain.membership.model.MembershipPlan;
import com.project.fitness.domain.payment.model.Payment;
import com.project.fitness.domain.payment.model.PaymentMethod;
import com.project.fitness.domain.payment.model.PaymentStatus;
import com.project.fitness.domain.user.model.User;
import com.project.fitness.domain.membership.repository.MembershipPlanRepository;
import com.project.fitness.domain.payment.repository.PaymentRepository;
import com.project.fitness.domain.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Stripe Payment Service Two-phase payment flow: 1. Frontend calls POST /api/payments/create-intent → gets client_secret 2. Frontend confirms with Stripe.js (card element) 3. Stripe calls webhook → backend activates membership
@Service
@RequiredArgsConstructor
@Slf4j
public class StripePaymentService implements IStripePaymentService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${stripe.webhook.secret:}")
    private String stripeWebhookSecret;

    private final MembershipPlanRepository planRepository;
    private final PaymentRepository paymentRepository;
    private final MembershipService membershipService;
    private final UserRepository userRepository;
    private final IEmailService emailService;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
        log.info("Stripe payment gateway initialized");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Phase 1: Create PaymentIntent (called by frontend before checkout UI)
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public CreatePaymentIntentResponse createPaymentIntent(
            String userId,
            CreatePaymentIntentRequest request) throws StripeException {

        MembershipPlan plan = planRepository.findById(request.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("MembershipPlan", "id", request.getPlanId()));

        if (!plan.isActive()) {
            throw new BadRequestException("Selected membership plan is no longer available.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Convert to smallest currency unit (paise for INR, cents for USD)
        long amountInSmallestUnit = plan.getPrice()
                .multiply(BigDecimal.valueOf(100))
                .longValue();

        // Store metadata to link back to membership on webhook
        Map<String, String> metadata = new HashMap<>();
        metadata.put("userId", userId);
        metadata.put("planId", plan.getId());
        metadata.put("userEmail", user.getEmail());
        metadata.put("planName", plan.getName());

        // Idempotency key: prevent double-charges if client retries
        String idempotencyKey = "pi-" + userId + "-" + plan.getId();

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInSmallestUnit)
                .setCurrency(request.getCurrency() != null ? request.getCurrency().toLowerCase() : "inr")
                .setDescription("Fitness Management System Membership \u2013 " + plan.getName())
                .putAllMetadata(metadata)
                .setReceiptEmail(user.getEmail())
                .addPaymentMethodType("card")
                .build();

        com.stripe.net.RequestOptions options = com.stripe.net.RequestOptions.builder()
                .setIdempotencyKey(idempotencyKey)
                .build();
        PaymentIntent intent = PaymentIntent.create(params, options);

        log.info("Created PaymentIntent id={} amount={} currency={} userId={} idempotencyKey={}",
                intent.getId(), amountInSmallestUnit, intent.getCurrency(), userId, idempotencyKey);

        return CreatePaymentIntentResponse.builder()
                .clientSecret(intent.getClientSecret())
                .paymentIntentId(intent.getId())
                .amount(plan.getPrice())
                .currency(intent.getCurrency().toUpperCase())
                .planName(plan.getName())
                .planId(plan.getId())
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Phase 2: Stripe Webhook – activate membership when payment succeeds
    // ─────────────────────────────────────────────────────────────────────────

    @Transactional
    public void handleWebhookEvent(String payload, String sigHeader) {
        Event event;
        try {
            if (stripeWebhookSecret != null && !stripeWebhookSecret.isBlank()) {
                event = Webhook.constructEvent(payload, sigHeader, stripeWebhookSecret);
            } else {
                // In development without webhook signing secret – parse directly
                event = Event.GSON.fromJson(payload, Event.class);
                log.warn("Webhook signature verification skipped (no secret configured)");
            }
        } catch (Exception e) {
            log.error("Webhook signature verification failed: {}", e.getMessage());
            throw new BadRequestException("Invalid Stripe webhook signature: " + e.getMessage());
        }

        log.info("Received Stripe event: type={} id={}", event.getType(), event.getId());

        switch (event.getType()) {
            case "payment_intent.succeeded" -> handlePaymentSucceeded(event);
            case "payment_intent.payment_failed" -> handlePaymentFailed(event);
            default -> log.debug("Unhandled Stripe event type: {}", event.getType());
        }
    }

    private void handlePaymentSucceeded(Event event) {
        PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                .getObject().orElseThrow(() -> new BadRequestException("Missing PaymentIntent object"));

        Map<String, String> meta = intent.getMetadata();
        String userId = meta.get("userId");
        String planId = meta.get("planId");

        log.info("Payment succeeded: paymentIntentId={} userId={} planId={}", intent.getId(), userId, planId);

        // Activate the membership via existing service (handles DB + notification)
        try {
            BuyMembershipRequest req = BuyMembershipRequest.builder()
                    .planId(planId)
                    .paymentMethod(PaymentMethod.CARD)
                    .build();

            MembershipPurchaseResponse result = membershipService.buyMembership(userId, true, req);

            // Update the payment record to store Stripe's IDs
            Payment payment = paymentRepository.findById(result.getPayment().getId()).orElse(null);
            if (payment != null) {
                payment.setTransactionId(intent.getId());
                payment.setGateway("STRIPE");
                payment.setStatus(PaymentStatus.SUCCESS);
                paymentRepository.save(payment);
            }

            log.info("Membership activated for userId={} planId={}", userId, planId);
        } catch (BadRequestException e) {
            // e.g. "already has an active membership" — log and skip
            log.warn("Membership activation skipped for userId={}: {}", userId, e.getMessage());
        }
    }

    private void handlePaymentFailed(Event event) {
        PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                .getObject().orElseThrow(() -> new BadRequestException("Missing PaymentIntent object"));

        Map<String, String> meta = intent.getMetadata();
        String userId = meta.get("userId");
        String planName = meta.getOrDefault("planName", "your plan");
        String reason = intent.getLastPaymentError() != null
                ? intent.getLastPaymentError().getMessage() : "Payment declined by bank";

        log.warn("Payment failed: paymentIntentId={} userId={} reason={}", intent.getId(), userId, reason);

        // Notify user via email
        userRepository.findById(userId).ifPresent(user ->
            emailService.sendPaymentFailed(user.getEmail(), user.getFirstName(), planName, reason)
        );
    }
}
