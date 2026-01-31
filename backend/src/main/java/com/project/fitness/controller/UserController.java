package com.project.fitness.controller;

import com.project.fitness.dto.ChangePasswordRequest;
import com.project.fitness.dto.UpdateProfileRequest;
import com.project.fitness.dto.UserResponse;
import com.project.fitness.exceptions.BadRequestException;
import com.project.fitness.exceptions.ResourceNotFoundException;
import com.project.fitness.exceptions.UnauthorizedException;
import com.project.fitness.model.User;
import com.project.fitness.repository.UserRepository;
import com.project.fitness.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
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
@RequestMapping("/api/users")
public class UserController {

  private final UserRepository userRepository;
  private final UserService userService;
  private final PasswordEncoder passwordEncoder;

  public UserController(UserRepository userRepository, UserService userService,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * Get current user profile
   */
  @GetMapping("/profile")
  public ResponseEntity<UserResponse> getProfile(Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    return ResponseEntity.ok(userService.MapToResponse(user));
  }

  /**
   * Update current user profile
   */
  @PutMapping("/profile")
  public ResponseEntity<UserResponse> updateProfile(
      Authentication authentication,
      @Valid @RequestBody UpdateProfileRequest request) {
    String userId = (String) authentication.getPrincipal();
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

    if (request.getFirstName() != null && !request.getFirstName().trim().isEmpty()) {
      user.setFirstName(request.getFirstName().trim());
    }
    if (request.getLastName() != null && !request.getLastName().trim().isEmpty()) {
      user.setLastName(request.getLastName().trim());
    }
    if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
      // Check if email is already taken by another user
      User existingUser = userRepository.findByEmail(request.getEmail().trim());
      if (existingUser != null && !existingUser.getId().equals(userId)) {
        throw new BadRequestException("Email already in use");
      }
      user.setEmail(request.getEmail().trim());
    }

    User savedUser = userRepository.save(user);
    return ResponseEntity.ok(userService.MapToResponse(savedUser));
  }

  /**
   * Change password
   */
  @PostMapping("/change-password")
  public ResponseEntity<Void> changePassword(
      Authentication authentication,
      @Valid @RequestBody ChangePasswordRequest request) {
    String userId = (String) authentication.getPrincipal();
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

    // Verify current password
    if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
      throw new UnauthorizedException("Current password is incorrect");
    }

    // Update password
    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(user);

    return ResponseEntity.ok().build();
  }

  /**
   * Get user by ID (Admin only)
   */
  @GetMapping("/{userId}")
  public ResponseEntity<UserResponse> getUserById(@PathVariable String userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));
    return ResponseEntity.ok(userService.MapToResponse(user));
  }

  /**
   * Search users
   */
  @GetMapping("/search")
  public ResponseEntity<org.springframework.data.domain.Page<UserResponse>> searchUsers(
      @RequestParam String query,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
    return ResponseEntity.ok(userService.searchUsers(query, pageable));
  }

  /**
   * Get all users (Admin only)
   */
  @GetMapping
  public ResponseEntity<org.springframework.data.domain.Page<UserResponse>> getAllUsers(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
    return ResponseEntity.ok(userService.getAllUsers(pageable));
  }

  /**
   * Delete user (Admin only)
   */
  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
    if (!userRepository.existsById(userId)) {
      throw new RuntimeException("User not found");
    }
    userRepository.deleteById(userId);
    return ResponseEntity.ok().build();
  }
}
