package com.project.fitness.domain.fitness.repository;

import com.project.fitness.domain.fitness.model.WorkoutExercise;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkoutExerciseRepository extends JpaRepository<WorkoutExercise, String> {

  List<WorkoutExercise> findByWorkoutPlanId(String workoutPlanId);
}
