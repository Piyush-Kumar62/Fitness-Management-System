package com.project.fitness.domain.user.service;
import com.project.fitness.domain.notification.service.IEmailService;

import com.project.fitness.domain.user.dto.*;
import com.project.fitness.common.exception.BadRequestException;
import com.project.fitness.common.exception.UnauthorizedException;
import com.project.fitness.common.exception.ResourceNotFoundException;
import com.project.fitness.domain.gym.model.Gym;
import com.project.fitness.domain.gym.model.GymStatus;
import com.project.fitness.domain.gym.repository.GymRepository;
import com.project.fitness.domain.user.model.AccountStatus;
import com.project.fitness.domain.user.model.User;
import com.project.fitness.domain.user.model.UserRole;
import com.project.fitness.domain.user.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import com.project.fitness.common.response.PagedResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

  private final UserRepository userRepository;
  private final GymRepository gymRepository;
  private final PasswordEncoder passwordEncoder;
  private final IEmailService emailService;

  public UserService(UserRepository userRepository, GymRepository gymRepository,
      PasswordEncoder passwordEncoder, IEmailService emailService) {
    this.userRepository = userRepository;
    this.gymRepository = gymRepository;
    this.passwordEncoder = passwordEncoder;
    this.emailService = emailService;
  }

  public UserResponse register(RegisterRequest request) {
    String email = request.getEmail().trim().toLowerCase();
    if (userRepository.findByEmail(email) != null) {
      throw new BadRequestException("Email already registered");
    }
    User user = buildNewUser(request, email);
    User saved = userRepository.save(user);
    createOwnerGymIfNeeded(request, saved.getId());
    emailService.sendWelcomeEmail(saved.getEmail(), saved.getFirstName());
    return mapToResponse(saved);
  }

  private User buildNewUser(RegisterRequest request, String email) {
    User user = new User();
    user.setEmail(email);
    user.setFirstName(request.getFirstName().trim());
    user.setLastName(request.getLastName().trim());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(isAllowedSelfRole(request.getRole()) ? request.getRole() : UserRole.MEMBER);
    user.setGymId(request.getGymId());
    user.setProvider("local");
    user.setStatus(AccountStatus.APPROVED);
    user.setEmailVerified(true);
    user.setActive(true);
    user.setPhone(request.getPhone());
    user.setDob(request.getDob());
    user.setGender(request.getGender());
    user.setPasswordResetRequired(false);
    return user;
  }

  private boolean isAllowedSelfRole(UserRole role) {
    return role == UserRole.OWNER || role == UserRole.MEMBER;
  }

  private void createOwnerGymIfNeeded(RegisterRequest request, String ownerId) {
    if (request.getRole() != UserRole.OWNER) return;
    if (request.getGymName() == null || request.getGymName().isBlank()) return;
    Gym gym = new Gym();
    gym.setName(request.getGymName().trim());
    gym.setAddress(request.getGymAddress());
    gym.setContact(request.getGymContact());
    gym.setOwnerId(ownerId);
    gym.setStatus(GymStatus.ACTIVE);
    gymRepository.save(gym);
  }

  public UserResponse createUser(CreateUserRequest request) {
    String email = request.getEmail().trim().toLowerCase();
    if (userRepository.findByEmail(email) != null) {
      throw new BadRequestException("Email already registered");
    }
    User user = buildAdminCreatedUser(request, email);
    User saved = userRepository.save(user);
    emailService.sendAccountCreated(saved.getEmail(), saved.getFirstName(), request.getPassword(), saved.getRole().name());
    return mapToResponse(saved);
  }

  private User buildAdminCreatedUser(CreateUserRequest request, String email) {
    User user = new User();
    user.setEmail(email);
    user.setFirstName(request.getFirstName().trim());
    user.setLastName(request.getLastName().trim());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(request.getRole());
    user.setProvider("local");
    user.setActive(true);
    user.setStatus(AccountStatus.APPROVED);
    user.setEmailVerified(true);
    return user;
  }

  @Transactional(readOnly = true)
  public User authenticate(LoginRequest loginRequest) {
    User user = userRepository.findByEmail(loginRequest.getEmail().trim().toLowerCase());
    if (user == null) throw new UnauthorizedException("Invalid email or password");
    if (user.getPassword() == null || user.getPassword().isBlank()) {
      throw new UnauthorizedException("This account uses social login. Please sign in with Google.");
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
    if (!user.isEmailVerified() && !user.isPasswordResetRequired()) {
      throw new UnauthorizedException("Email not verified. Please verify your email before logging in.");
    }
    AccountStatus status = user.getStatus() == null ? AccountStatus.APPROVED : user.getStatus();
    if (status != AccountStatus.APPROVED) {
      throw new UnauthorizedException("Account is pending approval. Please wait for an administrator to approve your account.");
    }
  }

  @Transactional(readOnly = true)
  public PagedResponse<UserResponse> searchUsers(String query, Pageable pageable) {
    return PagedResponse.from(userRepository.searchUsers(query, pageable).map(this::mapToResponse));
  }

  @Transactional(readOnly = true)
  public PagedResponse<UserResponse> getAllUsers(Pageable pageable) {
    return PagedResponse.from(userRepository.findAll(pageable).map(this::mapToResponse));
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
