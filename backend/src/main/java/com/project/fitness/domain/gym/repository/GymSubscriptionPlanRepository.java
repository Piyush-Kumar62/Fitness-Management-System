package com.project.fitness.domain.gym.repository;
import com.project.fitness.domain.gym.model.Gym;

import com.project.fitness.domain.gym.model.GymSubscriptionPlan;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GymSubscriptionPlanRepository extends JpaRepository<GymSubscriptionPlan, String> {

  List<GymSubscriptionPlan> findByActiveTrueOrderByMonthlyPriceAsc();
}
