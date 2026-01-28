package com.project.fitness.controller;

import com.project.fitness.dto.ChangePasswordRequest;
import com.project.fitness.dto.UpdateProfileRequest;
import com.project.fitness.dto.UserResponse;
import com.project.fitness.exceptions.BadRequestException;
import com.project.fitness.exceptions.ResourceNotFoundException;
import com.project.fitness.exceptions.UnauthorizedException;
import com.project.fitness.model.User;
import com.project.fitness.repository.UserRepository;
import com.project.fitness.service.IFileStorageService;
import com.project.fitness.service.UserService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller for user operations.
 * Updated to support profile picture uploads following Dependency Inversion Principle.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserRepository userRepository;
  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final IFileStorageService fileStorageService;

  /**
   * Upload profile picture
   */
  @PostMapping("/profile-picture")
  public ResponseEntity<?> uploadProfilePicture(
      @RequestParam("file") MultipartFile file,
      Authentication authentication
  ) {
    try {
      String userId = (String) authentication.getPrincipal();
      
      // Upload file
      String fileId = fileStorageService.storeFile(file, "PROFILE_PICTURE", userId);
      String fileUrl = fileStorageService.getFileUrl(fileId);
      
      // Update user
      User user = userRepository.findById(userId)
          .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
      
      // Delete old profile picture if exists
      if (user.getProfilePictureId() != null) {
        try {
          fileStorageService.deleteFile(user.getProfilePictureId());
        } catch (IOException e) {
          // Log but don't fail
        }
      }
      
      user.setProfilePictureId(fileId);
      userRepository.save(user);
      
      Map<String, Object> response = new HashMap<>();
      response.put("fileId", fileId);
      response.put("fileUrl", fileUrl);
      response.put("message", "Profile picture updated successfully");
      
      return ResponseEntity.ok(response);
    } catch (IOException e) {
      return ResponseEntity.badRequest()
          .body(Map.of("error", "Failed to upload profile picture"));
    }
  }

  /**
   * Get current user profile (includes profile picture URL)
   */
  @GetMapping("/profile")
  public ResponseEntity<UserResponse> getProfile(Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    
    UserResponse response = userService.MapToResponse(user);
    
    // Add profile picture URL if exists
    if (user.getProfilePictureId() != null) {
      response.setProfilePictureUrl(fileStorageService.getFileUrl(user.getProfilePictureId()));
    }
    
    return ResponseEntity.ok(response);
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

    if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
      throw new UnauthorizedException("Current password is incorrect");
    }

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
   * Get all users (Admin only)
   */
  @GetMapping
  public ResponseEntity<List<UserResponse>> getAllUsers() {
    List<User> users = userRepository.findAll();
    List<UserResponse> userResponses = users.stream()
        .map(userService::MapToResponse)
        .collect(Collectors.toList());
    return ResponseEntity.ok(userResponses);
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
