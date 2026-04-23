package com.project.fitness.domain.fitness.repository;
import com.project.fitness.domain.fitness.model.Activity;
import com.project.fitness.domain.user.model.User;

import com.project.fitness.domain.fitness.model.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecommendationRepository extends JpaRepository<Recommendation, String> {
  // Property traversal: Recommendation.user.id / Recommendation.activity.id
  List<Recommendation> findByUser_IdOrderByCreatedAtDesc(String userId);
  List<Recommendation> findByActivity_IdOrderByCreatedAtDesc(String activityId);
}