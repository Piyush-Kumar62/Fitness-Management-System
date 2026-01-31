package com.project.fitness.service;

import com.project.fitness.dto.BodyMeasurementRequest;
import com.project.fitness.dto.BodyMeasurementResponse;
import com.project.fitness.exceptions.BadRequestException;
import com.project.fitness.exceptions.ResourceNotFoundException;
import com.project.fitness.model.BodyMeasurement;
import com.project.fitness.model.FileUpload;
import com.project.fitness.model.User;
import com.project.fitness.repository.BodyMeasurementRepository;
import com.project.fitness.repository.FileUploadRepository;
import com.project.fitness.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BodyMeasurementService {

  private final BodyMeasurementRepository measurementRepository;
  private final UserRepository userRepository;
  private final FileUploadRepository fileUploadRepository;

  public BodyMeasurementService(BodyMeasurementRepository measurementRepository,
                                UserRepository userRepository,
                                FileUploadRepository fileUploadRepository) {
    this.measurementRepository = measurementRepository;
    this.userRepository = userRepository;
    this.fileUploadRepository = fileUploadRepository;
  }

  public BodyMeasurementResponse createMeasurement(BodyMeasurementRequest request, String userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    // Calculate BMI if weight and height are provided
    Double bmi = null;
    if (request.getWeight() != null && request.getHeight() != null && request.getHeight() > 0) {
      double heightInMeters = request.getHeight() / 100.0;
      bmi = request.getWeight() / (heightInMeters * heightInMeters);
      bmi = Math.round(bmi * 10.0) / 10.0; // Round to 1 decimal place
    }

    FileUpload photo = null;
    if (request.getPhotoId() != null) {
      photo = fileUploadRepository.findById(request.getPhotoId()).orElse(null);
    }

    BodyMeasurement measurement = BodyMeasurement.builder()
        .user(user)
        .measurementDate(request.getMeasurementDate())
        .weight(request.getWeight())
        .height(request.getHeight())
        .bodyFat(request.getBodyFat())
        .muscleMass(request.getMuscleMass())
        .bmi(bmi)
        .measurements(request.getMeasurements())
        .progressPhoto(photo)
        .notes(request.getNotes())
        .build();

    BodyMeasurement saved = measurementRepository.save(measurement);
    return mapToResponse(saved);
  }

  public BodyMeasurementResponse updateMeasurement(String id, BodyMeasurementRequest request, String userId) {
    BodyMeasurement measurement = measurementRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Measurement not found"));

    if (!measurement.getUser().getId().equals(userId)) {
      throw new BadRequestException("Unauthorized");
    }

    // Recalculate BMI
    Double bmi = null;
    if (request.getWeight() != null && request.getHeight() != null && request.getHeight() > 0) {
      double heightInMeters = request.getHeight() / 100.0;
      bmi = request.getWeight() / (heightInMeters * heightInMeters);
      bmi = Math.round(bmi * 10.0) / 10.0;
    }

    FileUpload photo = null;
    if (request.getPhotoId() != null) {
      photo = fileUploadRepository.findById(request.getPhotoId()).orElse(null);
    }

    measurement.setMeasurementDate(request.getMeasurementDate());
    measurement.setWeight(request.getWeight());
    measurement.setHeight(request.getHeight());
    measurement.setBodyFat(request.getBodyFat());
    measurement.setMuscleMass(request.getMuscleMass());
    measurement.setBmi(bmi);
    measurement.setMeasurements(request.getMeasurements());
    measurement.setProgressPhoto(photo);
    measurement.setNotes(request.getNotes());

    BodyMeasurement updated = measurementRepository.save(measurement);
    return mapToResponse(updated);
  }

  public BodyMeasurementResponse getMeasurementById(String id, String userId) {
    BodyMeasurement measurement = measurementRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Measurement not found"));

    if (!measurement.getUser().getId().equals(userId)) {
      throw new BadRequestException("Unauthorized");
    }

    return mapToResponse(measurement);
  }

  public List<BodyMeasurementResponse> getUserMeasurements(String userId) {
    return measurementRepository.findByUser_IdOrderByMeasurementDateDesc(userId).stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
  }

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
      photoUrl = "/api/files/" + measurement.getProgressPhoto().getId();
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
