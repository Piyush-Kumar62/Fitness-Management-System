package com.project.fitness.domain.trainer.service;
import com.project.fitness.domain.notification.service.IEmailService;
import com.project.fitness.domain.notification.service.NotificationService;
import com.project.fitness.domain.gym.model.Gym;

import com.project.fitness.domain.trainer.dto.AttendanceResponse;
import com.project.fitness.domain.trainer.dto.BookClassRequest;
import com.project.fitness.domain.trainer.dto.ClassBookingResponse;
import com.project.fitness.domain.trainer.dto.ClassScheduleResponse;
import com.project.fitness.domain.trainer.dto.CreateClassRequest;
import com.project.fitness.domain.trainer.dto.MarkAttendanceRequest;
import com.project.fitness.common.exception.BadRequestException;
import com.project.fitness.common.exception.ResourceNotFoundException;
import com.project.fitness.common.exception.UnauthorizedException;
import com.project.fitness.domain.trainer.model.Attendance;
import com.project.fitness.domain.trainer.model.ClassBooking;
import com.project.fitness.domain.trainer.model.ClassBookingStatus;
import com.project.fitness.domain.trainer.model.ClassSchedule;
import com.project.fitness.domain.user.model.User;
import com.project.fitness.domain.user.model.UserRole;
import com.project.fitness.domain.trainer.repository.AttendanceRepository;
import com.project.fitness.domain.trainer.repository.ClassBookingRepository;
import com.project.fitness.domain.trainer.repository.ClassScheduleRepository;
import com.project.fitness.domain.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ClassManagementService {

  private final ClassScheduleRepository classScheduleRepository;
  private final ClassBookingRepository classBookingRepository;
  private final AttendanceRepository attendanceRepository;
  private final UserRepository userRepository;
  private final NotificationService notificationService;
  private final IEmailService emailService;

  public ClassScheduleResponse createClass(String trainerId, CreateClassRequest request) {
    User trainer = getUser(trainerId, UserRole.TRAINER);
    validateClassWindow(request.getStartTime(), request.getEndTime());
    ClassSchedule schedule = ClassSchedule.builder()
        .gymId(requireGym(trainer))
        .trainerId(trainerId)
        .className(request.getClassName().trim())
        .startTime(request.getStartTime())
        .endTime(request.getEndTime())
        .capacity(request.getCapacity())
        .active(true)
        .build();
    return toScheduleResponse(classScheduleRepository.save(schedule));
  }

  @Transactional(readOnly = true)
  public List<ClassScheduleResponse> getTrainerClasses(String trainerId) {
    getUser(trainerId, UserRole.TRAINER);
    return classScheduleRepository.findByTrainerIdOrderByStartTimeDesc(trainerId).stream()
        .map(this::toScheduleResponse)
        .toList();
  }

  @Transactional(readOnly = true)
  public List<ClassScheduleResponse> getAvailableClasses(String memberId) {
    User member = getUser(memberId, UserRole.MEMBER);
    return classScheduleRepository.findByGymIdAndActiveTrueAndStartTimeAfterOrderByStartTimeAsc(
            requireGym(member), LocalDateTime.now())
        .stream()
        .map(this::toScheduleResponse)
        .toList();
  }

  public ClassBookingResponse bookClass(String memberId, BookClassRequest request) {
    User member = getUser(memberId, UserRole.MEMBER);
    ClassSchedule schedule = getSchedule(request.getClassId());
    assertSameGym(requireGym(member), schedule.getGymId());
    assertBookable(schedule, memberId);
    ClassBooking booking = ClassBooking.builder()
        .classId(schedule.getId())
        .memberId(memberId)
        .status(ClassBookingStatus.BOOKED)
        .build();
    ClassBooking saved = classBookingRepository.save(booking);
    // Real-time WebSocket notification
    notificationService.notifyUser(memberId, "CLASS_BOOKED", "Class Booked",
        "You booked " + schedule.getClassName() + " at " + schedule.getStartTime() + ".");
    // Async email confirmation
    String trainerName = userRepository.findById(schedule.getTrainerId())
        .map(t -> t.getFirstName() + " " + t.getLastName()).orElse("Your Trainer");
    emailService.sendClassBookingConfirmation(member.getEmail(), member.getFirstName(),
        schedule.getClassName(), schedule.getStartTime().toString(), trainerName);
    return toBookingResponse(saved);
  }

  @Transactional(readOnly = true)
  public List<ClassBookingResponse> getMemberBookings(String memberId) {
    getUser(memberId, UserRole.MEMBER);
    return classBookingRepository.findByMemberIdOrderByBookedAtDesc(memberId).stream()
        .map(this::toBookingResponse)
        .toList();
  }

  public AttendanceResponse markAttendance(String trainerId, MarkAttendanceRequest request) {
    User trainer = getUser(trainerId, UserRole.TRAINER);
    ClassSchedule schedule = getSchedule(request.getClassId());
    assertClassOwner(trainerId, schedule);
    User member = getUser(request.getMemberId(), UserRole.MEMBER);
    assertSameGym(requireGym(trainer), requireGym(member));
    Attendance attendance = attendanceRepository.findByClassIdAndMemberId(schedule.getId(), member.getId())
        .orElse(Attendance.builder()
            .classId(schedule.getId()).memberId(member.getId()).markedBy(trainerId).date(LocalDate.now()).build());
    attendance.setStatus(request.getStatus());
    notificationService.notifyUser(member.getId(), "ATTENDANCE", "Attendance Updated",
        "Your attendance for " + schedule.getClassName() + " is " + request.getStatus() + ".");
    return toAttendanceResponse(attendanceRepository.save(attendance));
  }

  @Transactional(readOnly = true)
  public List<ClassBookingResponse> getClassBookings(String trainerId, String classId) {
    getUser(trainerId, UserRole.TRAINER);
    assertClassOwner(trainerId, getSchedule(classId));
    return classBookingRepository.findByClassIdAndStatus(classId, ClassBookingStatus.BOOKED).stream()
        .map(this::toBookingResponse)
        .toList();
  }

  @Transactional(readOnly = true)
  public List<AttendanceResponse> getClassAttendance(String trainerId, String classId) {
    getUser(trainerId, UserRole.TRAINER);
    assertClassOwner(trainerId, getSchedule(classId));
    return attendanceRepository.findByClassId(classId).stream().map(this::toAttendanceResponse).toList();
  }

  private void assertBookable(ClassSchedule schedule, String memberId) {
    if (schedule.getStartTime().isBefore(LocalDateTime.now())) {
      throw new BadRequestException("Cannot book past classes");
    }
    if (classBookingRepository.existsByClassIdAndMemberIdAndStatus(
        schedule.getId(), memberId, ClassBookingStatus.BOOKED)) {
      throw new BadRequestException("Class already booked");
    }
    long booked = classBookingRepository.countByClassIdAndStatus(schedule.getId(), ClassBookingStatus.BOOKED);
    if (booked >= schedule.getCapacity()) {
      throw new BadRequestException("Class is full");
    }
  }

  private void validateClassWindow(LocalDateTime start, LocalDateTime end) {
    if (!end.isAfter(start)) {
      throw new BadRequestException("Class end time must be after start time");
    }
  }

  private void assertClassOwner(String trainerId, ClassSchedule schedule) {
    if (!trainerId.equals(schedule.getTrainerId())) {
      throw new UnauthorizedException("You can manage only your classes");
    }
  }

  private void assertSameGym(String gymA, String gymB) {
    if (!gymA.equals(gymB)) {
      throw new UnauthorizedException("Cross-gym operation is not allowed");
    }
  }

  private User getUser(String userId, UserRole role) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    if (user.getRole() != role) {
      throw new UnauthorizedException("User role mismatch for this action");
    }
    return user;
  }

  private String requireGym(User user) {
    if (user.getGymId() == null || user.getGymId().isBlank()) {
      throw new BadRequestException("User is not mapped to any gym");
    }
    return user.getGymId();
  }

  private ClassSchedule getSchedule(String classId) {
    return classScheduleRepository.findById(classId)
        .orElseThrow(() -> new ResourceNotFoundException("ClassSchedule", "id", classId));
  }

  private ClassScheduleResponse toScheduleResponse(ClassSchedule schedule) {
    User trainer = userRepository.findById(schedule.getTrainerId()).orElse(null);
    String trainerName = trainer == null ? "Unknown" : trainer.getFirstName() + " " + trainer.getLastName();
    long booked = classBookingRepository.countByClassIdAndStatus(schedule.getId(), ClassBookingStatus.BOOKED);
    return ClassScheduleResponse.builder()
        .id(schedule.getId()).gymId(schedule.getGymId()).trainerId(schedule.getTrainerId())
        .trainerName(trainerName).className(schedule.getClassName())
        .startTime(schedule.getStartTime()).endTime(schedule.getEndTime()).capacity(schedule.getCapacity())
        .bookedCount(booked).availableSlots(Math.max(schedule.getCapacity() - booked, 0))
        .build();
  }

  private ClassBookingResponse toBookingResponse(ClassBooking booking) {
    ClassSchedule schedule = classScheduleRepository.findById(booking.getClassId()).orElse(null);
    User member = userRepository.findById(booking.getMemberId()).orElse(null);
    String className = schedule == null ? "Unknown" : schedule.getClassName();
    String memberName = member == null ? "Unknown" : member.getFirstName() + " " + member.getLastName();
    return ClassBookingResponse.builder()
        .id(booking.getId()).classId(booking.getClassId()).className(className)
        .memberId(booking.getMemberId()).memberName(memberName)
        .status(booking.getStatus()).bookedAt(booking.getBookedAt())
        .build();
  }

  private AttendanceResponse toAttendanceResponse(Attendance attendance) {
    User member = userRepository.findById(attendance.getMemberId()).orElse(null);
    String memberName = member == null ? "Unknown" : member.getFirstName() + " " + member.getLastName();
    return AttendanceResponse.builder()
        .id(attendance.getId()).classId(attendance.getClassId()).memberId(attendance.getMemberId())
        .memberName(memberName).markedBy(attendance.getMarkedBy()).date(attendance.getDate())
        .status(attendance.getStatus())
        .build();
  }
}
