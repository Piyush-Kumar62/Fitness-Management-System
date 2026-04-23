package com.project.fitness.domain.user.service;
import com.project.fitness.domain.gym.model.Gym;
import com.project.fitness.domain.membership.model.Membership;

import com.project.fitness.common.exception.BadRequestException;
import com.project.fitness.common.exception.ResourceNotFoundException;
import com.project.fitness.domain.fitness.model.Activity;
import com.project.fitness.domain.trainer.model.ClassBookingStatus;
import com.project.fitness.domain.membership.model.MembershipStatus;
import com.project.fitness.domain.payment.model.Payment;
import com.project.fitness.domain.payment.model.PaymentStatus;
import com.project.fitness.domain.user.model.User;
import com.project.fitness.domain.user.model.UserRole;
import com.project.fitness.domain.fitness.repository.ActivityRepository;
import com.project.fitness.domain.trainer.repository.ClassScheduleRepository;
import com.project.fitness.domain.trainer.repository.ClassBookingRepository;
import com.project.fitness.domain.gym.repository.GymRepository;
import com.project.fitness.domain.membership.repository.MembershipRepository;
import com.project.fitness.domain.payment.repository.PaymentRepository;
import com.project.fitness.domain.fitness.repository.RecommendationRepository;
import com.project.fitness.domain.user.repository.UserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

  private final UserRepository userRepository;
  private final GymRepository gymRepository;
  private final MembershipRepository membershipRepository;
  private final PaymentRepository paymentRepository;
  private final ActivityRepository activityRepository;
  private final RecommendationRepository recommendationRepository;
  private final ClassScheduleRepository classScheduleRepository;
  private final ClassBookingRepository classBookingRepository;

  public Map<String, Object> getAdminDashboardStats() {
    List<Activity> activities = activityRepository.findAll();
    long totalUsers = userRepository.count();
    long totalActivities = activities.size();
    int totalCalories = activities.stream().map(Activity::getCaloriesBurned).filter(v -> v != null).mapToInt(v -> v).sum();
    long activeUsersToday = activities.stream().filter(a -> sameDate(a.getStartTime(), LocalDate.now()))
        .map(a -> a.getUser().getId()).distinct().count();
    long newUsersThisWeek = userRepository.findAll().stream().filter(u -> createdAfter(u, LocalDate.now().minusDays(7))).count();
    BigDecimal monthlyRevenue = sumAmount(paymentRepository.findByStatusAndCreatedAtBetween(
        PaymentStatus.SUCCESS, monthStart(), monthEnd()));
    Map<String, Object> stats = new HashMap<>();
    stats.put("totalGyms", gymRepository.count());
    stats.put("activeMemberships", membershipRepository.countByStatus(MembershipStatus.ACTIVE));
    stats.put("monthlyRevenue", monthlyRevenue);
    stats.put("systemUsers", totalUsers);
    stats.put("totalUsers", totalUsers);
    stats.put("totalMembers", userRepository.countByRole(UserRole.MEMBER));
    stats.put("totalTrainers", userRepository.countByRole(UserRole.TRAINER));
    stats.put("totalOwners", userRepository.countByRole(UserRole.OWNER));
    stats.put("totalAdmins", userRepository.countByRole(UserRole.ADMIN));
    stats.put("totalActivities", totalActivities);
    stats.put("totalCaloriesBurned", totalCalories);
    stats.put("avgActivitiesPerUser", totalUsers == 0 ? 0D : (double) totalActivities / totalUsers);
    stats.put("activeUsersToday", activeUsersToday);
    stats.put("newUsersThisWeek", newUsersThisWeek);
    return stats;
  }

  public Map<String, Object> getOwnerDashboardStats(String ownerId) {
    List<String> gymIds = gymRepository.findByOwnerId(ownerId).stream().map(g -> g.getId()).toList();
    if (gymIds.isEmpty()) {
      throw new BadRequestException("Owner has no gym assigned");
    }
    List<String> memberIds = userRepository.findByGymIdIn(gymIds).stream()
        .filter(u -> u.getRole() == UserRole.MEMBER).map(User::getId).toList();
    LocalDateTime start = monthStart();
    LocalDateTime end = monthEnd();
    BigDecimal revenue = sumAmount(paymentRepository.findByMemberIdInAndStatusAndCreatedAtBetween(
        memberIds, PaymentStatus.SUCCESS, start, end));
    Map<String, Object> stats = new HashMap<>();
    stats.put("totalMembers", userRepository.countByGymIdInAndRole(gymIds, UserRole.MEMBER));
    stats.put("activeMemberships", membershipRepository.findByMemberIdInAndStatus(memberIds, MembershipStatus.ACTIVE).size());
    stats.put("trainers", userRepository.countByGymIdInAndRole(gymIds, UserRole.TRAINER));
    stats.put("todayClasses", gymIds.stream().mapToLong(g -> classScheduleRepository.countByGymIdAndStartTimeBetween(
        g, todayStart(), todayEnd())).sum());
    stats.put("monthlyRevenue", revenue);
    return stats;
  }

  public Map<String, Object> getTrainerDashboardStats(String trainerId) {
    User trainer = getUser(trainerId);
    if (trainer.getRole() != UserRole.TRAINER) {
      throw new BadRequestException("Only trainers can access trainer dashboard stats");
    }
    return Map.of("totalMembers", userRepository.findByTrainerId(trainerId).size());
  }

  public Map<String, Object> getMemberDashboardStats(String memberId) {
    User member = getUser(memberId);
    if (member.getRole() != UserRole.MEMBER) {
      throw new BadRequestException("Only members can access member dashboard stats");
    }
    List<Activity> activities = activityRepository.findByUser_Id(memberId);
    int calories = activities.stream().map(Activity::getCaloriesBurned).filter(v -> v != null).mapToInt(v -> v).sum();
    Map<String, Object> stats = new HashMap<>();
    stats.put("activities", activities.size());
    stats.put("calories", calories);
    stats.put("recommendations", recommendationRepository.findByUser_IdOrderByCreatedAtDesc(memberId).size());
    stats.put("bookedClasses", classBookingRepository.countByMemberIdAndStatus(
        memberId, ClassBookingStatus.BOOKED));
    return stats;
  }

  private User getUser(String userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
  }

  private boolean sameDate(LocalDateTime value, LocalDate date) {
    return value != null && value.toLocalDate().equals(date);
  }

  private boolean createdAfter(User user, LocalDate date) {
    return user.getCreatedAt() != null && !user.getCreatedAt().toLocalDate().isBefore(date);
  }

  private LocalDateTime monthStart() {
    LocalDate now = LocalDate.now();
    return now.withDayOfMonth(1).atStartOfDay();
  }

  private LocalDateTime monthEnd() {
    return monthStart().plusMonths(1).minusNanos(1);
  }

  private LocalDateTime todayStart() {
    return LocalDate.now().atStartOfDay();
  }

  private LocalDateTime todayEnd() {
    return todayStart().plusDays(1).minusNanos(1);
  }

  private BigDecimal sumAmount(List<Payment> payments) {
    return payments.stream().map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
