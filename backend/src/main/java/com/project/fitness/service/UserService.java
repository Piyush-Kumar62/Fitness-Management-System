package com.project.fitness.service;

import com.project.fitness.dto.LoginRequest;
import com.project.fitness.dto.RegisterRequest;
import com.project.fitness.dto.UserResponse;
import com.project.fitness.exceptions.BadRequestException;
import com.project.fitness.exceptions.ResourceNotFoundException;
import com.project.fitness.exceptions.UnauthorizedException;
import com.project.fitness.model.User;
import com.project.fitness.model.UserRole;
import com.project.fitness.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public UserResponse register(RegisterRequest request) {
    // Check if user already exists
    if (userRepository.findByEmail(request.getEmail()) != null) {
      throw new BadRequestException("Email already registered");
    }
    
    User user = new User();
    user.setEmail(request.getEmail().trim().toLowerCase());
    user.setFirstName(request.getFirstName().trim());
    user.setLastName(request.getLastName().trim());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(request.getRole() != null ? request.getRole() : UserRole.USER);
    return MapToResponse(userRepository.save(user));
  }

  public User authenticate(LoginRequest loginRequest) {
    User user = userRepository.findByEmail(loginRequest.getEmail().trim().toLowerCase());
    if (user == null) {
      throw new UnauthorizedException("Invalid email or password");
    }
    if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
      throw new UnauthorizedException("Invalid email or password");
    }
    return user;
  }

  public org.springframework.data.domain.Page<UserResponse> searchUsers(String query, org.springframework.data.domain.Pageable pageable) {
    return userRepository.searchUsers(query, pageable)
        .map(this::MapToResponse);
  }

  public org.springframework.data.domain.Page<UserResponse> getAllUsers(org.springframework.data.domain.Pageable pageable) {
    return userRepository.findAll(pageable)
        .map(this::MapToResponse);
  }

  public UserResponse MapToResponse(User savedUser) {
    UserResponse response = new UserResponse();
    response.setId(savedUser.getId());
    response.setEmail(savedUser.getEmail());
    response.setPassword(null); // NEVER return the password/hash to the client
    response.setFirstName(savedUser.getFirstName());
    response.setLastName(savedUser.getLastName());
    response.setRole(savedUser.getRole());
    response.setCreatedAt(savedUser.getCreatedAt());
    response.setUpdatedAt(savedUser.getUpdatedAt());
    return response;
  }
}