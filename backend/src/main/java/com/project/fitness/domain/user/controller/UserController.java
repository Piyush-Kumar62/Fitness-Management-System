package com.project.fitness.domain.user.controller;

import com.project.fitness.domain.user.dto.ChangePasswordRequest;
import com.project.fitness.domain.user.dto.UpdateProfileRequest;
import com.project.fitness.domain.user.dto.UpdateUserRequest;
import com.project.fitness.domain.user.dto.UserResponse;
import com.project.fitness.modules.user.application.UserApplicationService;
import jakarta.validation.Valid;
import com.project.fitness.common.response.PagedResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

  private final UserApplicationService userApplicationService;

  public UserController(UserApplicationService userApplicationService) {
    this.userApplicationService = userApplicationService;
  }

  // Get current user profile (any authenticated role)
  @GetMapping("/profile")
  public ResponseEntity<UserResponse> getProfile(Authentication authentication) {
    return ResponseEntity.ok(userApplicationService.getProfile((String) authentication.getPrincipal()));
  }

  // Update current user profile (any authenticated role)
  @PutMapping("/profile")
  public ResponseEntity<UserResponse> updateProfile(
      Authentication authentication,
      @Valid @RequestBody UpdateProfileRequest request) {
    return ResponseEntity.ok(
        userApplicationService.updateProfile((String) authentication.getPrincipal(), request));
  }

  // Upload profile image (any authenticated role)
  @PostMapping("/profile/image")
  public ResponseEntity<UserResponse> uploadProfileImage(
      @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
      Authentication authentication) {
    return ResponseEntity.ok(
        userApplicationService.uploadProfileImage((String) authentication.getPrincipal(), file));
  }

  // Delete profile image (any authenticated role)
  @DeleteMapping("/profile/image")
  public ResponseEntity<UserResponse> deleteProfileImage(Authentication authentication) {
    return ResponseEntity.ok(
        userApplicationService.deleteProfileImage((String) authentication.getPrincipal()));
  }

  // Change password (any authenticated role)
  @PostMapping("/change-password")
  public ResponseEntity<Void> changePassword(
      Authentication authentication,
      @Valid @RequestBody ChangePasswordRequest request) {
    userApplicationService.changePassword((String) authentication.getPrincipal(), request);
    return ResponseEntity.ok().build();
  }

  // Get user by ID (Admin only)
  @GetMapping("/{userId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponse> getUserById(@PathVariable String userId) {
    return ResponseEntity.ok(userApplicationService.getUserById(userId));
  }

  // Create user (Admin only)
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponse> createUser(@Valid @RequestBody com.project.fitness.domain.user.dto.CreateUserRequest request) {
    return ResponseEntity.ok(userApplicationService.createUser(request));
  }

  // Update user by ID (Admin only)
  @PutMapping("/{userId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UserResponse> updateUserById(
      @PathVariable String userId,
      @Valid @RequestBody UpdateUserRequest request) {
    return ResponseEntity.ok(userApplicationService.updateUserById(userId, request));
  }

  // Search users (Admin only)
  @GetMapping("/search")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<PagedResponse<UserResponse>> searchUsers(
      @RequestParam String query,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(userApplicationService.searchUsers(query, pageable));
  }

  // Get all users (Admin only)
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<PagedResponse<UserResponse>> getAllUsers(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(userApplicationService.getAllUsers(pageable));
  }

  // Delete user (Admin only)
  @DeleteMapping("/{userId}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
    userApplicationService.deleteUser(userId);
    return ResponseEntity.ok().build();
  }
}
