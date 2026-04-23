package com.project.fitness.domain.user.service;
import com.project.fitness.domain.notification.service.IEmailService;

import com.project.fitness.domain.user.dto.*;
import com.project.fitness.common.exception.BadRequestException;
import com.project.fitness.common.exception.UnauthorizedException;
import com.project.fitness.common.exception.ResourceNotFoundException;
import com.project.fitness.domain.user.model.AccountStatus;
import com.project.fitness.domain.user.model.User;
import com.project.fitness.domain.user.model.UserRole;
import com.project.fitness.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.project.fitness.common.response.PagedResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final IEmailService emailService;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, IEmailService emailService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.emailService = emailService;
  }

  public UserResponse register(RegisterRequest request) {
    String normalizedEmail = request.getEmail().trim().toLowerCase();
    // Check if user already exists
    if (userRepository.findByEmail(normalizedEmail) != null) {
      throw new BadRequestException("Email already registered");
    }

    User user = new User();
    user.setEmail(normalizedEmail);
    user.setFirstName(request.getFirstName().trim());
    user.setLastName(request.getLastName().trim());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    // Prevent role escalation during self-registration
    user.setRole(UserRole.MEMBER);
    user.setGymId(request.getGymId());
    user.setProvider("local");
    user.setStatus(AccountStatus.PENDING);
    user.setEmailVerified(false);
    user.setActive(true);
    User saved = userRepository.save(user);
    // Send async welcome email
    emailService.sendWelcomeEmail(saved.getEmail(), saved.getFirstName());
    return mapToResponse(saved);
  }

  public UserResponse createUser(com.project.fitness.domain.user.dto.CreateUserRequest request) {
    String normalizedEmail = request.getEmail().trim().toLowerCase();
    // Check if user already exists
    if (userRepository.findByEmail(normalizedEmail) != null) {
      throw new BadRequestException("Email already registered");
    }

    User user = new User();
    user.setEmail(normalizedEmail);
    user.setFirstName(request.getFirstName().trim());
    user.setLastName(request.getLastName().trim());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(request.getRole());
    user.setProvider("local");
    user.setActive(true);
    user.setStatus(AccountStatus.APPROVED);
    user.setEmailVerified(true);
    User saved = userRepository.save(user);
    // Notify the newly created user via email with their temporary password
    emailService.sendAccountCreated(saved.getEmail(), saved.getFirstName(), request.getPassword(), saved.getRole().name());
    return mapToResponse(saved);
  }

  @Transactional(readOnly = true)
  public User authenticate(LoginRequest loginRequest) {
    User user = userRepository.findByEmail(loginRequest.getEmail().trim().toLowerCase());
    if (user == null) {
      throw new UnauthorizedException("Invalid email or password");
    }
    if (user.getPassword() == null || user.getPassword().isBlank()) {
      throw new UnauthorizedException("This account uses social login. Please sign in with Google or GitHub.");
    }
    if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
      throw new UnauthorizedException("Invalid email or password");
    }
    assertLoginAllowed(user);
    return user;
  }

  public void assertLoginAllowed(User user) {
    if (!user.isActive()) {
      throw new UnauthorizedException("Account is deactivated. Please contact an administrator.");
    }
    if (!user.isEmailVerified()) {
      throw new UnauthorizedException("Email not verified. Please verify your email before logging in.");
    }
    AccountStatus status = user.getStatus() == null ? AccountStatus.APPROVED : user.getStatus();
    if (status != AccountStatus.APPROVED) {
      throw new UnauthorizedException("Account is pending approval.");
    }
  }

  @Transactional(readOnly = true)
  public PagedResponse<UserResponse> searchUsers(String query, Pageable pageable) {
    return PagedResponse.from(userRepository.searchUsers(query, pageable)
        .map(this::mapToResponse));
  }

  @Transactional(readOnly = true)
  public PagedResponse<UserResponse> getAllUsers(Pageable pageable) {
    return PagedResponse.from(userRepository.findAll(pageable)
        .map(this::mapToResponse));
  }

  @Transactional(readOnly = true)
  public User getUserById(String id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
  }

  public UserResponse mapToResponse(User user) {
    return UserResponse.builder()
        .id(user.getId())
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .role(user.getEffectiveRole())
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .provider(user.getProvider())
        .profileImageUrl(user.getProfileImageUrl())
        .trainerId(user.getTrainerId())
        .gymId(user.getGymId())
        .status(user.getStatus())
        .emailVerified(user.isEmailVerified())
        .profileComplete(user.isProfileComplete())
        .active(user.isActive())
        .dob(user.getDob())
        .gender(user.getGender())
        .phone(user.getPhone())
        .build();
  }
}
