package com.project.fitness.domain.trainer.repository;

import com.project.fitness.domain.trainer.model.ClassSchedule;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, String> {

  List<ClassSchedule> findByTrainerIdOrderByStartTimeDesc(String trainerId);

  List<ClassSchedule> findByGymIdAndActiveTrueAndStartTimeAfterOrderByStartTimeAsc(
      String gymId, LocalDateTime now);

  long countByGymIdAndStartTimeBetween(String gymId, LocalDateTime start, LocalDateTime end);
}
