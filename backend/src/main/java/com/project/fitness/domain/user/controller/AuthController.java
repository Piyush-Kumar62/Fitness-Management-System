package com.project.fitness.domain.user.controller;

import com.project.fitness.common.exception.UnauthorizedException;
import com.project.fitness.domain.user.dto.CompleteProfileRequest;
import com.project.fitness.domain.user.dto.LoginRequest;
import com.project.fitness.domain.user.dto.LoginResponse;
import com.project.fitness.domain.user.dto.RegisterRequest;
import com.project.fitness.domain.user.dto.UserResponse;
import com.project.fitness.modules.user.application.UserApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final UserApplicationService userApplicationService;

  public AuthController(UserApplicationService userApplicationService) {
    this.userApplicationService = userApplicationService;
  }

  @PostMapping("/register")
  public ResponseEntity<UserResponse> register(
      @Valid @RequestBody RegisterRequest registerRequest) {
    return ResponseEntity.ok(userApplicationService.register(registerRequest));
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
    return ResponseEntity.ok(userApplicationService.login(loginRequest));
  }

  @PostMapping("/reset-password")
  public ResponseEntity<Void> resetFirstPassword(
      Authentication authentication,
      @Valid @RequestBody com.project.fitness.domain.user.dto.ResetPasswordRequest request) {
    if (authentication == null || authentication.getPrincipal() == null) {
      throw new UnauthorizedException("Authentication is required");
    }
    String userId = (String) authentication.getPrincipal();
    userApplicationService.resetFirstPassword(userId, request);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/complete-profile")
  public ResponseEntity<UserResponse> completeProfile(
      Authentication authentication,
      @Valid @RequestBody CompleteProfileRequest request) {
    if (authentication == null || authentication.getPrincipal() == null) {
      throw new UnauthorizedException("Authentication is required");
    }
    String userId = (String) authentication.getPrincipal();
    return ResponseEntity.ok(userApplicationService.completeProfile(userId, request.getRole()));
  }
}
