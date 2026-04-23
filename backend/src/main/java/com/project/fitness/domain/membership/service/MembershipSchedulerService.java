package com.project.fitness.domain.membership.service;
import com.project.fitness.domain.notification.service.IEmailService;

import com.project.fitness.domain.membership.model.Membership;
import com.project.fitness.domain.membership.model.MembershipStatus;
import com.project.fitness.domain.membership.model.MembershipPlan;
import com.project.fitness.domain.user.model.User;
import com.project.fitness.domain.membership.repository.MembershipRepository;
import com.project.fitness.domain.membership.repository.MembershipPlanRepository;
import com.project.fitness.domain.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MembershipSchedulerService {

    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final MembershipPlanRepository planRepository;
    private final IEmailService emailService;

    // Run every day at 08:00 AM server time
    @Scheduled(cron = "0 0 8 * * ?")
    @Transactional(readOnly = true)
    public void sendExpiryReminders() {
        LocalDate expiryDate = LocalDate.now().plusDays(7);
        log.info("Running membership expiry check for date: {}", expiryDate);

        List<Membership> expiringMemberships = membershipRepository.findByStatusAndEndDate(MembershipStatus.ACTIVE, expiryDate);
        
        for (Membership membership : expiringMemberships) {
            try {
                User user = userRepository.findById(membership.getMemberId()).orElse(null);
                MembershipPlan plan = planRepository.findById(membership.getPlanId()).orElse(null);
                
                if (user != null && plan != null) {
                    emailService.sendMembershipExpiryReminder(
                        user.getEmail(),
                        user.getFirstName(),
                        plan.getName(),
                        membership.getEndDate().toString(),
                        7 // days left
                    );
                    log.info("Sent expiry reminder to {} for membership id {}", user.getEmail(), membership.getId());
                }
            } catch (Exception e) {
                log.error("Failed to process expiry reminder for membership id {}: {}", membership.getId(), e.getMessage());
            }
        }
    }
}
