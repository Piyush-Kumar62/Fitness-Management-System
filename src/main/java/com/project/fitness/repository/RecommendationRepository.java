package com.project.fitness.repository;

import com.project.fitness.model.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecommendationRepository extends JpaRepository<Recommendation, String> {
  // Property traversal: Recommendation.user.id / Recommendation.activity.id
  List<Recommendation> findByUser_IdOrderByCreatedAtDesc(String userId);
  List<Recommendation> findByActivity_IdOrderByCreatedAtDesc(String activityId);
}