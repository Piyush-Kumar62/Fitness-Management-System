package com.project.fitness.domain.fitness.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.project.fitness.domain.fitness.dto.ActivityRequest;
import com.project.fitness.domain.fitness.dto.ActivityResponse;
import com.project.fitness.common.response.PagedResponse;
import com.project.fitness.domain.fitness.model.Activity;
import com.project.fitness.domain.fitness.model.ActivityType;
import com.project.fitness.domain.user.model.User;
import com.project.fitness.domain.fitness.repository.ActivityRepository;
import com.project.fitness.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ActivityService activityService;

    private User testUser;
    private Activity testActivity;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("user1");
        testUser.setEmail("test@example.com");

        testActivity = new Activity();
        testActivity.setId("activity1");
        testActivity.setUser(testUser);
        testActivity.setType(ActivityType.RUNNING);
        testActivity.setDuration(30);
        testActivity.setCaloriesBurned(300);
        testActivity.setStartTime(LocalDateTime.now());
    }

    @Test
    void trackActivity_Success() {
        ActivityRequest request = new ActivityRequest();
        request.setUserId("user1");
        request.setType(ActivityType.RUNNING);
        request.setDuration(30);
        request.setCaloriesBurned(300);
        request.setStartTime(LocalDateTime.now());

        when(userRepository.findById(anyString())).thenReturn(Optional.of(testUser));
        when(activityRepository.save(any(Activity.class))).thenReturn(testActivity);

        ActivityResponse response = activityService.trackActivity(request);

        assertNotNull(response);
        assertEquals(ActivityType.RUNNING, response.getType());
        assertEquals(30, response.getDuration());
        assertEquals("user1", response.getUserId());
        verify(activityRepository, times(1)).save(any(Activity.class));
    }

    @Test
    void trackActivity_UserNotFound_ThrowsException() {
        ActivityRequest request = new ActivityRequest();
        request.setUserId("invalid-user");

        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> activityService.trackActivity(request));
        verify(activityRepository, never()).save(any(Activity.class));
    }

    @Test
    void getAllSystemActivities_ReturnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Activity> activityPage = new PageImpl<>(Arrays.asList(testActivity));

        when(activityRepository.findAll(pageable)).thenReturn(activityPage);

        PagedResponse<ActivityResponse> result = activityService.getAllSystemActivities(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(ActivityType.RUNNING, result.getContent().get(0).getType());
    }
}
