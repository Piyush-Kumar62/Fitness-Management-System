package com.project.fitness.domain.user.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.project.fitness.domain.user.dto.CreateUserRequest;
import com.project.fitness.domain.user.dto.RegisterRequest;
import com.project.fitness.domain.user.dto.UserResponse;
import com.project.fitness.common.exception.BadRequestException;
import com.project.fitness.domain.user.model.User;
import com.project.fitness.domain.user.model.UserRole;
import com.project.fitness.domain.user.repository.UserRepository;
import com.project.fitness.domain.notification.service.IEmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private IEmailService emailService;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("1");
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setPassword("encodedPassword");
        testUser.setRole(UserRole.MEMBER);
        testUser.setActive(true);
    }

    @Test
    void register_NewUser_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@example.com");
        request.setFirstName("New");
        request.setLastName("User");
        request.setPassword("password123");

        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User user = (User) i.getArguments()[0];
            user.setId("new-id");
            return user;
        });

        UserResponse response = userService.register(request);

        assertNotNull(response);
        assertEquals("new@example.com", response.getEmail());
        assertEquals(UserRole.MEMBER, response.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_ExistingUser_ThrowsException() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setFirstName("Test");
        request.setLastName("User");
        request.setPassword("password123");

        when(userRepository.findByEmail(anyString())).thenReturn(testUser);

        assertThrows(BadRequestException.class, () -> userService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_AdminCreatingTrainer_Success() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("trainer@example.com");
        request.setFirstName("Trainer");
        request.setLastName("User");
        request.setPassword("password123");
        request.setRole(UserRole.TRAINER);

        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User user = (User) i.getArguments()[0];
            user.setId("trainer-id");
            return user;
        });

        UserResponse response = userService.createUser(request);

        assertNotNull(response);
        assertEquals("trainer@example.com", response.getEmail());
        assertEquals(UserRole.TRAINER, response.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }
}
