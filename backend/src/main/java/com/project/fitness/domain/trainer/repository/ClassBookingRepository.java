package com.project.fitness.domain.trainer.repository;

import com.project.fitness.domain.trainer.model.ClassBooking;
import com.project.fitness.domain.trainer.model.ClassBookingStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassBookingRepository extends JpaRepository<ClassBooking, String> {

  boolean existsByClassIdAndMemberIdAndStatus(String classId, String memberId, ClassBookingStatus status);

  long countByClassIdAndStatus(String classId, ClassBookingStatus status);

  List<ClassBooking> findByMemberIdOrderByBookedAtDesc(String memberId);

  long countByMemberIdAndStatus(String memberId, ClassBookingStatus status);

  List<ClassBooking> findByClassIdAndStatus(String classId, ClassBookingStatus status);
}
