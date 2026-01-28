package com.project.fitness.controller;

import com.project.fitness.dto.LoginRequest;
import com.project.fitness.dto.LoginResponse;
import com.project.fitness.dto.RegisterRequest;
import com.project.fitness.dto.UserResponse;
import com.project.fitness.exceptions.UnauthorizedException;
import com.project.fitness.model.User;
import com.project.fitness.security.JwtUtils;
import com.project.fitness.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final UserService userService;
  //  private final userRepository userRepository;
//  private final passwordEncoder passwordEncoder;
  private final JwtUtils jwtUtils;

  public AuthController(UserService userService, JwtUtils jwtUtils) {
    this.userService = userService;
    this.jwtUtils = jwtUtils;
  }

  @PostMapping("/register")
  public ResponseEntity<UserResponse> register(
      @Valid @RequestBody RegisterRequest registerRequest) {
    // validation
    return ResponseEntity.ok(userService.register(registerRequest));
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
    User user = userService.authenticate(loginRequest);
    String token = jwtUtils.generateToken(user.getId(), user.getRole().name());
    return ResponseEntity.ok(
        new LoginResponse(token, userService.MapToResponse(user))
    );
  }
}
