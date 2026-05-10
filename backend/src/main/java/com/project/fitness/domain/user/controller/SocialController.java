package com.project.fitness.domain.user.controller;

import com.project.fitness.domain.fitness.model.Activity;
import com.project.fitness.domain.fitness.repository.ActivityRepository;
import com.project.fitness.domain.user.model.User;
import com.project.fitness.domain.user.model.UserFollow;
import com.project.fitness.domain.user.repository.UserFollowRepository;
import com.project.fitness.domain.user.repository.UserRepository;
import com.project.fitness.common.exception.ResourceNotFoundException;
import com.project.fitness.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/api/v1/social")
@RequiredArgsConstructor
public class SocialController {

  private final UserFollowRepository userFollowRepository;
  private final UserRepository userRepository;
  private final ActivityRepository activityRepository;

  @GetMapping("/feed")
  public ResponseEntity<List<Activity>> getSocialFeed(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    
    List<UserFollow> follows = userFollowRepository.findByFollower_Id(userId);
    List<String> followingIds = follows.stream()
        .map(f -> f.getFollowing().getId())
        .collect(Collectors.toList());
    
    // Include current user's activities in the feed too
    followingIds.add(userId);
    
    List<Activity> feed = activityRepository.findByUser_IdIn(
        followingIds, 
        PageRequest.of(page, size, Sort.by("startTime").descending())
    ).getContent();
    
    return ResponseEntity.ok(feed);
  }

  @PostMapping("/follow/{followingId}")
  public ResponseEntity<Void> followUser(@PathVariable String followingId, Authentication authentication) {
    String followerId = (String) authentication.getPrincipal();
    
    if (followerId.equals(followingId)) {
      throw new BadRequestException("You cannot follow yourself");
    }
    
    if (userFollowRepository.existsByFollower_IdAndFollowing_Id(followerId, followingId)) {
      throw new BadRequestException("You are already following this user");
    }
    
    User follower = userRepository.findById(followerId)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", followerId));
    User following = userRepository.findById(followingId)
        .orElseThrow(() -> new ResourceNotFoundException("User", "id", followingId));
    
    UserFollow follow = UserFollow.builder()
        .follower(follower)
        .following(following)
        .build();
    
    userFollowRepository.save(follow);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/unfollow/{followingId}")
  public ResponseEntity<Void> unfollowUser(@PathVariable String followingId, Authentication authentication) {
    String followerId = (String) authentication.getPrincipal();
    
    UserFollow follow = userFollowRepository.findByFollower_IdAndFollowing_Id(followerId, followingId)
        .orElseThrow(() -> new ResourceNotFoundException("Follow", "relationship", followingId));
    
    userFollowRepository.delete(follow);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/followers/{userId}")
  public ResponseEntity<List<String>> getFollowers(@PathVariable String userId) {
    List<UserFollow> follows = userFollowRepository.findByFollowing_Id(userId);
    List<String> followerIds = follows.stream()
        .map(f -> f.getFollower().getId())
        .collect(Collectors.toList());
    return ResponseEntity.ok(followerIds);
  }

  @GetMapping("/following/{userId}")
  public ResponseEntity<List<String>> getFollowing(@PathVariable String userId) {
    List<UserFollow> follows = userFollowRepository.findByFollower_Id(userId);
    List<String> followingIds = follows.stream()
        .map(f -> f.getFollowing().getId())
        .collect(Collectors.toList());
    return ResponseEntity.ok(followingIds);
  }
}
