package com.project.fitness.domain.trainer.controller;
import com.project.fitness.domain.trainer.model.Attendance;

import com.project.fitness.domain.trainer.dto.AttendanceResponse;
import com.project.fitness.domain.trainer.dto.ClassBookingResponse;
import com.project.fitness.domain.trainer.dto.ClassScheduleResponse;
import com.project.fitness.domain.trainer.dto.CreateClassRequest;
import com.project.fitness.domain.trainer.dto.MarkAttendanceRequest;
import com.project.fitness.domain.trainer.service.ClassManagementService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trainer/classes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TRAINER')")
public class TrainerClassController {

  private final ClassManagementService classManagementService;

  @PostMapping
  public ResponseEntity<ClassScheduleResponse> createClass(
      Authentication authentication, @Valid @RequestBody CreateClassRequest request) {
    return ResponseEntity.ok(classManagementService.createClass((String) authentication.getPrincipal(), request));
  }

  @GetMapping
  public ResponseEntity<List<ClassScheduleResponse>> getMyClasses(Authentication authentication) {
    return ResponseEntity.ok(classManagementService.getTrainerClasses((String) authentication.getPrincipal()));
  }

  @PostMapping("/attendance")
  public ResponseEntity<AttendanceResponse> markAttendance(
      Authentication authentication, @Valid @RequestBody MarkAttendanceRequest request) {
    return ResponseEntity.ok(classManagementService.markAttendance((String) authentication.getPrincipal(), request));
  }

  @GetMapping("/{classId}/attendance")
  public ResponseEntity<List<AttendanceResponse>> getClassAttendance(
      Authentication authentication, @PathVariable String classId) {
    return ResponseEntity.ok(classManagementService.getClassAttendance((String) authentication.getPrincipal(), classId));
  }

  @GetMapping("/{classId}/bookings")
  public ResponseEntity<List<ClassBookingResponse>> getClassBookings(
      Authentication authentication, @PathVariable String classId) {
    return ResponseEntity.ok(classManagementService.getClassBookings((String) authentication.getPrincipal(), classId));
  }
}
