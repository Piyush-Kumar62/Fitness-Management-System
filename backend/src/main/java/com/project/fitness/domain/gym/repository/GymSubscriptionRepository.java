package com.project.fitness.domain.gym.repository;
import com.project.fitness.domain.gym.model.Gym;

import com.project.fitness.domain.gym.model.GymSubscription;
import com.project.fitness.domain.gym.model.GymSubscriptionStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GymSubscriptionRepository extends JpaRepository<GymSubscription, String> {

  Optional<GymSubscription> findFirstByGymIdAndStatusOrderByCreatedAtDesc(
      String gymId, GymSubscriptionStatus status);
}
