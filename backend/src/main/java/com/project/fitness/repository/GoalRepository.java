package com.project.fitness.repository;

import com.project.fitness.model.Goal;
import com.project.fitness.model.GoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, String> {
  List<Goal> findByUser_Id(String userId);
  List<Goal> findByUser_IdAndStatus(String userId, GoalStatus status);
  List<Goal> findByUser_IdOrderByCreatedAtDesc(String userId);
}
