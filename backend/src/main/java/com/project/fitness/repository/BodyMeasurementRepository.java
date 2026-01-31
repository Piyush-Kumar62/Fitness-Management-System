package com.project.fitness.repository;

import com.project.fitness.model.BodyMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BodyMeasurementRepository extends JpaRepository<BodyMeasurement, String> {
  List<BodyMeasurement> findByUser_IdOrderByMeasurementDateDesc(String userId);
  List<BodyMeasurement> findByUser_IdAndMeasurementDateBetween(
      String userId, LocalDate startDate, LocalDate endDate);
}
