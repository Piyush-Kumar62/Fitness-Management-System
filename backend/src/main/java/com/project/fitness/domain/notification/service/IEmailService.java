package com.project.fitness.domain.notification.service;
import com.project.fitness.domain.payment.model.PaymentMethod;

// Contract for email notification operations. Implementations are async and must not block the caller.
public interface IEmailService {

  void sendWelcomeEmail(String toEmail, String firstName);

  void sendPaymentConfirmation(String toEmail, String firstName,
      String planName, String amount, String transactionId,
      String validUntil, String paymentMethod);

  void sendPaymentFailed(String toEmail, String firstName,
      String planName, String reason);

  void sendMembershipActivated(String toEmail, String firstName,
      String planName, String startDate, String endDate);

  void sendMembershipExpiryReminder(String toEmail, String firstName,
      String planName, String expiryDate, int daysLeft);

  void sendClassBookingConfirmation(String toEmail, String firstName,
      String className, String scheduleDate, String trainerName);

  void sendGymSubscriptionConfirmation(String toEmail, String ownerName,
      String gymName, String planName, String amount,
      String validUntil, String transactionId);

  void sendPasswordChangedNotification(String toEmail, String firstName);

  void sendAccountCreated(String toEmail, String firstName,
      String temporaryPassword, String role);

  /** Sent immediately on registration: tells user to wait for admin approval. */
  void sendRegistrationPending(String toEmail, String firstName,
      String temporaryPassword, String role);

  /** Sent when admin approves the account – user can now log in. */
  void sendAccountApproved(String toEmail, String firstName, String role);
}
