package com.project.fitness.repository;

import com.project.fitness.model.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, String> {
  List<Milestone> findByGoal_Id(String goalId);
  List<Milestone> findByGoal_IdOrderByTargetValueAsc(String goalId);
}
