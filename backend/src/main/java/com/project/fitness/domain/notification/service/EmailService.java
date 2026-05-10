package com.project.fitness.domain.notification.service;
import com.project.fitness.domain.payment.model.PaymentMethod;
import com.project.fitness.domain.payment.model.Payment;
import com.project.fitness.domain.gym.model.Gym;
import com.project.fitness.domain.membership.model.Membership;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

// Central async email dispatch service. All sends are @Async so they do NOT block request threads. Implements {@link IEmailService} for testability.
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService implements IEmailService {

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  @Value("${spring.mail.username:noreply@fitness.app}")
  private String fromAddress;

  @Value("${app.name:Fitness Management System}")
  private String appName;

  @Override
  @Async
  public void sendWelcomeEmail(String toEmail, String firstName) {
    sendHtml(toEmail, "Welcome to " + appName + "! 🏋️", "email/welcome", buildBase(firstName));
  }

  @Override
  @Async
  public void sendPaymentConfirmation(String toEmail, String firstName, String planName,
      String amount, String transactionId, String validUntil, String paymentMethod) {
    Context ctx = buildBase(firstName);
    ctx.setVariable("planName", planName);
    ctx.setVariable("amount", amount);
    ctx.setVariable("transactionId", transactionId);
    ctx.setVariable("validUntil", validUntil);
    ctx.setVariable("paymentMethod", paymentMethod);
    sendHtml(toEmail, "Payment Confirmed – " + planName + " ✅", "email/payment-confirmation", ctx);
  }

  @Override
  @Async
  public void sendPaymentFailed(String toEmail, String firstName, String planName, String reason) {
    Context ctx = buildBase(firstName);
    ctx.setVariable("planName", planName);
    ctx.setVariable("reason", reason);
    sendHtml(toEmail, "Payment Failed – " + planName + " ❌", "email/payment-failed", ctx);
  }

  @Override
  @Async
  public void sendMembershipActivated(String toEmail, String firstName, String planName,
      String startDate, String endDate) {
    Context ctx = buildBase(firstName);
    ctx.setVariable("planName", planName);
    ctx.setVariable("startDate", startDate);
    ctx.setVariable("endDate", endDate);
    sendHtml(toEmail, "Your " + planName + " Membership is Active! 🎉", "email/membership-activated", ctx);
  }

  @Override
  @Async
  public void sendMembershipExpiryReminder(String toEmail, String firstName, String planName,
      String expiryDate, int daysLeft) {
    Context ctx = buildBase(firstName);
    ctx.setVariable("planName", planName);
    ctx.setVariable("expiryDate", expiryDate);
    ctx.setVariable("daysLeft", daysLeft);
    sendHtml(toEmail, "Your Membership Expires in " + daysLeft + " Day(s) ⏰",
        "email/membership-expiry-reminder", ctx);
  }

  @Override
  @Async
  public void sendClassBookingConfirmation(String toEmail, String firstName, String className,
      String scheduleDate, String trainerName) {
    Context ctx = buildBase(firstName);
    ctx.setVariable("className", className);
    ctx.setVariable("scheduleDate", scheduleDate);
    ctx.setVariable("trainerName", trainerName);
    sendHtml(toEmail, "Class Booking Confirmed – " + className + " 📅",
        "email/class-booking-confirmation", ctx);
  }

  @Override
  @Async
  public void sendGymSubscriptionConfirmation(String toEmail, String ownerName, String gymName,
      String planName, String amount, String validUntil, String transactionId) {
    Context ctx = buildBase(ownerName);
    ctx.setVariable("gymName", gymName);
    ctx.setVariable("planName", planName);
    ctx.setVariable("amount", amount);
    ctx.setVariable("validUntil", validUntil);
    ctx.setVariable("transactionId", transactionId);
    sendHtml(toEmail, gymName + " – Subscription Confirmed ✅", "email/gym-subscription-confirmation", ctx);
  }

  @Override
  @Async
  public void sendPasswordChangedNotification(String toEmail, String firstName) {
    sendHtml(toEmail, "Your Password Has Been Changed 🔐", "email/password-changed", buildBase(firstName));
  }

  @Override
  @Async
  public void sendAccountCreated(String toEmail, String firstName,
      String temporaryPassword, String role) {
    Context ctx = buildBase(firstName);
    ctx.setVariable("temporaryPassword", temporaryPassword);
    ctx.setVariable("role", role);
    sendHtml(toEmail, "Your Fitness Management System Account is Ready 🚀", "email/account-created", ctx);
  }

  @Override
  @Async
  public void sendRegistrationPending(String toEmail, String firstName,
      String temporaryPassword, String role) {
    Context ctx = buildBase(firstName);
    ctx.setVariable("temporaryPassword", temporaryPassword);
    ctx.setVariable("role", role);
    sendHtml(toEmail, "Registration Pending Approval ⏳", "email/registration-pending", ctx);
  }

  @Override
  @Async
  public void sendAccountApproved(String toEmail, String firstName, String role) {
    Context ctx = buildBase(firstName);
    ctx.setVariable("role", role);
    sendHtml(toEmail, "Account Approved! 🎉", "email/account-approved", ctx);
  }

  // ── Internals ──────────────────────────────────────────────

  private void sendHtml(String to, String subject, String template, Context ctx) {
    try {
      MimeMessage msg = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
      helper.setFrom(Objects.requireNonNullElse(fromAddress, "noreply@fitness.app"), appName);
      helper.setTo(Objects.requireNonNullElse(to, ""));
      helper.setSubject(Objects.requireNonNullElse(subject, "Fitness Management System Notification"));
      helper.setText(templateEngine.process(template, ctx), true);
      mailSender.send(msg);
      log.info("Email sent to={} subject=\"{}\"", to, subject);
    } catch (MessagingException | java.io.UnsupportedEncodingException ex) {
      log.error("Failed to send email to={} subject=\"{}\" error={}", to, subject, ex.getMessage());
    }
  }

  private Context buildBase(String recipientName) {
    Context ctx = new Context();
    ctx.setVariable("recipientName", recipientName);
    ctx.setVariable("appName", appName);
    ctx.setVariable("currentYear", java.time.Year.now().getValue());
    ctx.setVariable("supportEmail", fromAddress);
    return ctx;
  }
}
