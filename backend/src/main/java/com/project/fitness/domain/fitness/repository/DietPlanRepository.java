package com.project.fitness.domain.fitness.repository;

import com.project.fitness.domain.fitness.model.DietPlan;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DietPlanRepository extends JpaRepository<DietPlan, String> {

  List<DietPlan> findByTrainerId(String trainerId);
  Page<DietPlan> findByTrainerId(String trainerId, Pageable pageable);

  long countByTrainerId(String trainerId);
}
