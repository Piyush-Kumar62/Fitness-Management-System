package com.project.fitness.modules.user.application;

import com.project.fitness.common.exception.BadRequestException;
import com.project.fitness.common.exception.ResourceNotFoundException;
import com.project.fitness.common.exception.UnauthorizedException;
import com.project.fitness.common.response.PagedResponse;
import com.project.fitness.domain.notification.service.IEmailService;
import com.project.fitness.domain.trainer.service.TrainerService;
import com.project.fitness.domain.user.dto.ChangePasswordRequest;
import com.project.fitness.domain.user.dto.CreateUserRequest;
import com.project.fitness.domain.user.dto.FileUploadResponse;
import com.project.fitness.domain.user.dto.LoginRequest;
import com.project.fitness.domain.user.dto.LoginResponse;
import com.project.fitness.domain.user.dto.RegisterRequest;
import com.project.fitness.domain.user.dto.UpdateUserRequest;
import com.project.fitness.domain.user.dto.UpdateProfileRequest;
import com.project.fitness.domain.user.dto.UserResponse;
import com.project.fitness.domain.user.model.AccountStatus;
import com.project.fitness.domain.user.model.User;
import com.project.fitness.domain.user.model.UserRole;
import com.project.fitness.domain.user.repository.UserRepository;
import com.project.fitness.domain.user.service.DashboardService;
import com.project.fitness.domain.user.service.FileUploadService;
import com.project.fitness.domain.user.service.UserService;
import com.project.fitness.security.JwtUtils;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class UserApplicationService {

  private final UserRepository userRepository;
  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final IEmailService emailService;
  private final FileUploadService fileUploadService;
  private final DashboardService dashboardService;
  private final TrainerService trainerService;
  private final JwtUtils jwtUtils;

  public UserResponse register(RegisterRequest request) {
    return userService.register(request);
  }

  @Transactional(readOnly = true)
  public LoginResponse login(LoginRequest request) {
    User user = userService.authenticate(request);
    String token = jwtUtils.generateToken(user.getId(), user.getEffectiveRole().name());
    return new LoginResponse(token, userService.mapToResponse(user));
  }

  public UserResponse completeProfile(String userId, UserRole selectedRole) {
    if (selectedRole == null) {
      throw new BadRequestException("Role is required");
    }
    if (selectedRole == UserRole.ADMIN) {
      throw new UnauthorizedException("Admin role cannot be self-selected");
    }

    User user = userService.getUserById(userId);
    if ("local".equalsIgnoreCase(user.getProvider())) {
      throw new UnauthorizedException("Role selection is only available for social login onboarding");
    }
    if (user.getStatus() != AccountStatus.PENDING) {
      throw new BadRequestException("Profile completion is only allowed while account is pending");
    }
    if (user.getRole() != null) {
      throw new BadRequestException("Role has already been selected");
    }

    user.setRole(selectedRole);
    user.setProfileComplete(true);
    userRepository.save(user);
    return userService.mapToResponse(user);
  }

  @Transactional(readOnly = true)
  public UserResponse getProfile(String userId) {
    return userService.mapToResponse(userService.getUserById(userId));
  }

  public UserResponse updateProfile(String userId, UpdateProfileRequest request) {
    User user = userService.getUserById(userId);

    if (request.getFirstName() != null && !request.getFirstName().trim().isEmpty()) {
      user.setFirstName(request.getFirstName().trim());
    }
    if (request.getLastName() != null && !request.getLastName().trim().isEmpty()) {
      user.setLastName(request.getLastName().trim());
    }
    if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
      String normalizedEmail = request.getEmail().trim().toLowerCase();
      User existingUser = userRepository.findByEmail(normalizedEmail);
      if (existingUser != null && !existingUser.getId().equals(userId)) {
        throw new BadRequestException("Email already in use");
      }
      user.setEmail(normalizedEmail);
    }

    return userService.mapToResponse(userRepository.save(user));
  }

  public UserResponse uploadProfileImage(String userId, MultipartFile file) {
    FileUploadResponse fileResponse = fileUploadService.uploadFile(file, userId);
    User user = userService.getUserById(userId);
    user.setProfileImageUrl(fileResponse.getFileUrl());
    return userService.mapToResponse(userRepository.save(user));
  }

  public UserResponse deleteProfileImage(String userId) {
    User user = userService.getUserById(userId);
    if (user.getProfileImageUrl() != null) {
      String url = user.getProfileImageUrl();
      if (url.contains("/api/v1/files/") || url.contains("/api/files/")) {
        String[] parts = url.split("/");
        String fileId = parts[parts.length - 1];
        try {
          fileUploadService.deleteFile(fileId, userId);
        } catch (Exception ignored) {
          // Non-blocking best-effort cleanup.
        }
      }
      user.setProfileImageUrl(null);
      userRepository.save(user);
    }
    return userService.mapToResponse(user);
  }

  public void changePassword(String userId, ChangePasswordRequest request) {
    User user = userService.getUserById(userId);
    if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
      throw new UnauthorizedException("Current password is incorrect");
    }
    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(user);
    emailService.sendPasswordChangedNotification(user.getEmail(), user.getFirstName());
  }

  @Transactional(readOnly = true)
  public UserResponse getUserById(String userId) {
    return userService.mapToResponse(userService.getUserById(userId));
  }

  public UserResponse createUser(CreateUserRequest request) {
    return userService.createUser(request);
  }

  public UserResponse updateUserById(String userId, UpdateUserRequest request) {
    User user = userService.getUserById(userId);

    if (request.getFirstName() != null && !request.getFirstName().trim().isEmpty()) {
      user.setFirstName(request.getFirstName().trim());
    }
    if (request.getLastName() != null && !request.getLastName().trim().isEmpty()) {
      user.setLastName(request.getLastName().trim());
    }
    if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
      String normalizedEmail = request.getEmail().trim().toLowerCase();
      User existingUser = userRepository.findByEmail(normalizedEmail);
      if (existingUser != null && !existingUser.getId().equals(userId)) {
        throw new BadRequestException("Email already in use");
      }
      user.setEmail(normalizedEmail);
    }
    if (request.getPassword() != null && !request.getPassword().isBlank()) {
      user.setPassword(passwordEncoder.encode(request.getPassword()));
    }
    if (request.getRole() != null) {
      user.setRole(request.getRole());
    }
    if (request.getActive() != null) {
      user.setActive(request.getActive());
    }

    return userService.mapToResponse(userRepository.save(user));
  }

  @Transactional(readOnly = true)
  public PagedResponse<UserResponse> searchUsers(String query, Pageable pageable) {
    return userService.searchUsers(query, pageable);
  }

  @Transactional(readOnly = true)
  public PagedResponse<UserResponse> getAllUsers(Pageable pageable) {
    return userService.getAllUsers(pageable);
  }

  public void deleteUser(String userId) {
    if (!userRepository.existsById(userId)) {
      throw new ResourceNotFoundException("User", "id", userId);
    }
    userRepository.deleteById(userId);
  }

  @Transactional(readOnly = true)
  public Map<String, Object> getAdminDashboardStats() {
    return dashboardService.getAdminDashboardStats();
  }

  @Transactional(readOnly = true)
  public Map<String, Object> getOwnerDashboardStats(String ownerId) {
    return dashboardService.getOwnerDashboardStats(ownerId);
  }

  @Transactional(readOnly = true)
  public Map<String, Object> getTrainerDashboardStats(String trainerId) {
    return trainerService.getDashboardStats(trainerId);
  }

  @Transactional(readOnly = true)
  public Map<String, Object> getMemberDashboardStats(String memberId) {
    return dashboardService.getMemberDashboardStats(memberId);
  }

  public FileUploadResponse uploadFile(MultipartFile file, String userId) {
    return fileUploadService.uploadFile(file, userId);
  }

  @Transactional(readOnly = true)
  public com.project.fitness.domain.user.model.FileUpload getFileEntity(String id) {
    return fileUploadService.getFileEntity(id);
  }

  @Transactional(readOnly = true)
  public java.util.List<FileUploadResponse> getUserFiles(String userId) {
    return fileUploadService.getUserFiles(userId);
  }

  public void deleteFile(String id, String userId) {
    fileUploadService.deleteFile(id, userId);
  }
}
