package com.project.fitness.domain.fitness.service;

import com.project.fitness.domain.fitness.dto.BodyMeasurementRequest;
import com.project.fitness.domain.fitness.dto.BodyMeasurementResponse;
import com.project.fitness.common.exception.BadRequestException;
import com.project.fitness.common.exception.ResourceNotFoundException;
import com.project.fitness.domain.fitness.model.BodyMeasurement;
import com.project.fitness.domain.user.model.FileUpload;
import com.project.fitness.domain.user.model.User;
import com.project.fitness.domain.fitness.repository.BodyMeasurementRepository;
import com.project.fitness.domain.user.repository.FileUploadRepository;
import com.project.fitness.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BodyMeasurementService {

  private final BodyMeasurementRepository measurementRepository;
  private final UserRepository userRepository;
  private final FileUploadRepository fileUploadRepository;

  public BodyMeasurementResponse createMeasurement(BodyMeasurementRequest request, String userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    BodyMeasurement measurement = BodyMeasurement.builder()
        .user(user)
        .measurementDate(request.getMeasurementDate())
        .weight(request.getWeight()).height(request.getHeight())
        .bodyFat(request.getBodyFat()).muscleMass(request.getMuscleMass())
        .bmi(calculateBmi(request.getWeight(), request.getHeight()))
        .measurements(request.getMeasurements())
        .progressPhoto(resolvePhoto(request.getPhotoId()))
        .notes(request.getNotes()).build();
    return mapToResponse(measurementRepository.save(measurement));
  }

  public BodyMeasurementResponse updateMeasurement(String id, BodyMeasurementRequest request, String userId) {
    BodyMeasurement measurement = measurementRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Measurement not found"));
    if (!measurement.getUser().getId().equals(userId)) throw new BadRequestException("Unauthorized");
    applyMeasurementUpdates(measurement, request);
    return mapToResponse(measurementRepository.save(measurement));
  }

  private void applyMeasurementUpdates(BodyMeasurement measurement, BodyMeasurementRequest request) {
    measurement.setMeasurementDate(request.getMeasurementDate());
    measurement.setWeight(request.getWeight());
    measurement.setHeight(request.getHeight());
    measurement.setBodyFat(request.getBodyFat());
    measurement.setMuscleMass(request.getMuscleMass());
    measurement.setBmi(calculateBmi(request.getWeight(), request.getHeight()));
    measurement.setMeasurements(request.getMeasurements());
    measurement.setProgressPhoto(resolvePhoto(request.getPhotoId()));
    measurement.setNotes(request.getNotes());
  }

  private Double calculateBmi(Double weight, Double height) {
    if (weight == null || height == null || height <= 0) return null;
    double hm = height / 100.0;
    return Math.round(weight / (hm * hm) * 10.0) / 10.0;
  }

  private FileUpload resolvePhoto(String photoId) {
    if (photoId == null) return null;
    return fileUploadRepository.findById(photoId).orElse(null);
  }

  @Transactional(readOnly = true)
  public BodyMeasurementResponse getMeasurementById(String id, String userId) {
    BodyMeasurement measurement = measurementRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Measurement not found"));

    if (!measurement.getUser().getId().equals(userId)) {
      throw new BadRequestException("Unauthorized");
    }

    return mapToResponse(measurement);
  }

  @Transactional(readOnly = true)
  public List<BodyMeasurementResponse> getUserMeasurements(String userId) {
    return measurementRepository.findByUser_IdOrderByMeasurementDateDesc(userId).stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<BodyMeasurementResponse> getMeasurementsByDateRange(String userId, LocalDate startDate, LocalDate endDate) {
    return measurementRepository.findByUser_IdAndMeasurementDateBetween(userId, startDate, endDate).stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
  }

  public void deleteMeasurement(String id, String userId) {
    BodyMeasurement measurement = measurementRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Measurement not found"));

    if (!measurement.getUser().getId().equals(userId)) {
      throw new BadRequestException("Unauthorized");
    }

    measurementRepository.delete(measurement);
  }

  private BodyMeasurementResponse mapToResponse(BodyMeasurement measurement) {
    String photoUrl = null;
    if (measurement.getProgressPhoto() != null) {
      photoUrl = "/api/v1/files/" + measurement.getProgressPhoto().getId();
    }

    return new BodyMeasurementResponse(
        measurement.getId(),
        measurement.getUser().getId(),
        measurement.getMeasurementDate(),
        measurement.getWeight(),
        measurement.getHeight(),
        measurement.getBodyFat(),
        measurement.getMuscleMass(),
        measurement.getBmi(),
        measurement.getMeasurements(),
        photoUrl,
        measurement.getNotes(),
        measurement.getCreatedAt()
    );
  }
}
