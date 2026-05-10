package com.project.fitness.domain.user.repository;

import com.project.fitness.domain.user.model.UserFollow;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, String> {
  List<UserFollow> findByFollower_Id(String followerId);
  List<UserFollow> findByFollowing_Id(String followingId);
  Optional<UserFollow> findByFollower_IdAndFollowing_Id(String followerId, String followingId);
  boolean existsByFollower_IdAndFollowing_Id(String followerId, String followingId);
  long countByFollower_Id(String followerId);
  long countByFollowing_Id(String followingId);
  void deleteByFollower_IdAndFollowing_Id(String followerId, String followingId);
}
