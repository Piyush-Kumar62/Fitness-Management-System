package com.project.fitness.domain.trainer.controller;

import com.project.fitness.domain.trainer.dto.BookClassRequest;
import com.project.fitness.domain.trainer.dto.ClassBookingResponse;
import com.project.fitness.domain.trainer.dto.ClassScheduleResponse;
import com.project.fitness.domain.trainer.service.ClassManagementService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/member/classes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MEMBER')")
public class MemberClassController {

  private final ClassManagementService classManagementService;

  @GetMapping("/available")
  public ResponseEntity<List<ClassScheduleResponse>> getAvailableClasses(Authentication authentication) {
    return ResponseEntity.ok(classManagementService.getAvailableClasses((String) authentication.getPrincipal()));
  }

  @PostMapping("/book")
  public ResponseEntity<ClassBookingResponse> bookClass(
      Authentication authentication, @Valid @RequestBody BookClassRequest request) {
    return ResponseEntity.ok(classManagementService.bookClass((String) authentication.getPrincipal(), request));
  }

  @GetMapping("/bookings")
  public ResponseEntity<List<ClassBookingResponse>> getMyBookings(Authentication authentication) {
    return ResponseEntity.ok(classManagementService.getMemberBookings((String) authentication.getPrincipal()));
  }
}
