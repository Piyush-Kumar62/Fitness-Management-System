package com.project.fitness.domain.trainer.repository;

import com.project.fitness.domain.trainer.model.Attendance;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, String> {

  List<Attendance> findByClassId(String classId);

  Optional<Attendance> findByClassIdAndMemberId(String classId, String memberId);
}
