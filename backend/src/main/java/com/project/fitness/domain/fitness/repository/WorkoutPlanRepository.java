package com.project.fitness.domain.fitness.repository;

import com.project.fitness.domain.fitness.model.WorkoutPlan;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, String> {

  List<WorkoutPlan> findByTrainerId(String trainerId);
  Page<WorkoutPlan> findByTrainerId(String trainerId, Pageable pageable);

  long countByTrainerId(String trainerId);
}
