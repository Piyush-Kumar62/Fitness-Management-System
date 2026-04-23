package com.project.fitness.domain.notification.service;

import com.project.fitness.domain.membership.model.Membership;
import com.project.fitness.domain.membership.model.MembershipStatus;
import com.project.fitness.domain.membership.repository.MembershipRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationSchedulerService {

  private final MembershipRepository membershipRepository;
  private final NotificationService notificationService;

  @Scheduled(cron = "0 0 9 * * *")
  @Transactional(readOnly = true)
  public void sendMembershipExpiryReminders() {
    LocalDate targetDate = LocalDate.now().plusDays(3);
    membershipRepository.findAll().stream()
        .filter(membership -> membership.getStatus() == MembershipStatus.ACTIVE)
        .filter(membership -> targetDate.equals(membership.getEndDate()))
        .forEach(this::notifyExpiryReminder);
  }

  private void notifyExpiryReminder(Membership membership) {
    notificationService.notifyUser(
        membership.getMemberId(),
        "REMINDER",
        "Membership Expiry Reminder",
        "Your membership expires on " + membership.getEndDate() + ". Renew to stay active.");
  }
}
