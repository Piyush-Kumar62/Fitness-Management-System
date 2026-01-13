package com.project.fitness.service;

import com.project.fitness.dto.LoginRequest;
import com.project.fitness.dto.RegisterRequest;
import com.project.fitness.dto.UserResponse;
import com.project.fitness.model.User;
import com.project.fitness.model.UserRole;
import com.project.fitness.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public UserResponse register(RegisterRequest request) {
    User user = new User();
    user.setEmail(request.getEmail());
    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(request.getRole() != null ? request.getRole() : UserRole.USER);
    return MapToResponse(userRepository.save(user));
  }

  public User authenticate(LoginRequest loginRequest) {
    User user = userRepository.findByEmail(loginRequest.getEmail());
    if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
      throw new RuntimeException("Invalid Credentials");
    }
    return user;
  }

  public UserResponse MapToResponse(User savedUser) {
    UserResponse response = new UserResponse();
    response.setId(savedUser.getId());
    response.setEmail(savedUser.getEmail());
    response.setPassword(null); // NEVER return the password/hash to the client
    response.setFirstName(savedUser.getFirstName());
    response.setLastName(savedUser.getLastName());
    response.setCreatedAt(savedUser.getCreatedAt());
    response.setUpdatedAt(savedUser.getUpdatedAt());
    return response;
  }
}