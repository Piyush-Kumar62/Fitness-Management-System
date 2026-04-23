package com.project.fitness.domain.fitness.controller;

import com.project.fitness.domain.fitness.dto.BodyMeasurementRequest;
import com.project.fitness.domain.fitness.dto.BodyMeasurementResponse;
import com.project.fitness.modules.fitness.application.FitnessApplicationService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/measurements")
public class BodyMeasurementController {

  private final FitnessApplicationService fitnessApplicationService;

  public BodyMeasurementController(FitnessApplicationService fitnessApplicationService) {
    this.fitnessApplicationService = fitnessApplicationService;
  }

  @PostMapping
  public ResponseEntity<BodyMeasurementResponse> createMeasurement(
      @Valid @RequestBody BodyMeasurementRequest request,
      Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    return ResponseEntity.ok(fitnessApplicationService.createMeasurement(request, userId));
  }

  @PutMapping("/{id}")
  public ResponseEntity<BodyMeasurementResponse> updateMeasurement(
      @PathVariable String id,
      @Valid @RequestBody BodyMeasurementRequest request,
      Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    return ResponseEntity.ok(fitnessApplicationService.updateMeasurement(id, request, userId));
  }

  @GetMapping("/{id}")
  public ResponseEntity<BodyMeasurementResponse> getMeasurement(
      @PathVariable String id,
      Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    return ResponseEntity.ok(fitnessApplicationService.getMeasurementById(id, userId));
  }

  @GetMapping
  public ResponseEntity<List<BodyMeasurementResponse>> getUserMeasurements(
      Authentication authentication,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
    String userId = (String) authentication.getPrincipal();
    return ResponseEntity.ok(fitnessApplicationService.getMeasurements(userId, startDate, endDate));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteMeasurement(
      @PathVariable String id,
      Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    fitnessApplicationService.deleteMeasurement(id, userId);
    return ResponseEntity.noContent().build();
  }
}
