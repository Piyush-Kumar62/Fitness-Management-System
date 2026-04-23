package com.project.fitness.domain.gym.repository;

import com.project.fitness.domain.gym.model.Gym;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GymRepository extends JpaRepository<Gym, String> {

  boolean existsByNameIgnoreCase(String name);

  List<Gym> findByOwnerId(String ownerId);
}
