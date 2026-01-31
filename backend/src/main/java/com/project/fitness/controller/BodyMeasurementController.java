package com.project.fitness.controller;

import com.project.fitness.dto.BodyMeasurementRequest;
import com.project.fitness.dto.BodyMeasurementResponse;
import com.project.fitness.service.BodyMeasurementService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/measurements")
public class BodyMeasurementController {

  private final BodyMeasurementService measurementService;

  public BodyMeasurementController(BodyMeasurementService measurementService) {
    this.measurementService = measurementService;
  }

  @PostMapping
  public ResponseEntity<BodyMeasurementResponse> createMeasurement(
      @Valid @RequestBody BodyMeasurementRequest request,
      Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    return ResponseEntity.ok(measurementService.createMeasurement(request, userId));
  }

  @PutMapping("/{id}")
  public ResponseEntity<BodyMeasurementResponse> updateMeasurement(
      @PathVariable String id,
      @Valid @RequestBody BodyMeasurementRequest request,
      Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    return ResponseEntity.ok(measurementService.updateMeasurement(id, request, userId));
  }

  @GetMapping("/{id}")
  public ResponseEntity<BodyMeasurementResponse> getMeasurement(
      @PathVariable String id,
      Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    return ResponseEntity.ok(measurementService.getMeasurementById(id, userId));
  }

  @GetMapping
  public ResponseEntity<List<BodyMeasurementResponse>> getUserMeasurements(
      Authentication authentication,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
    String userId = (String) authentication.getPrincipal();
    
    if (startDate != null && endDate != null) {
      return ResponseEntity.ok(measurementService.getMeasurementsByDateRange(userId, startDate, endDate));
    }
    
    return ResponseEntity.ok(measurementService.getUserMeasurements(userId));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteMeasurement(
      @PathVariable String id,
      Authentication authentication) {
    String userId = (String) authentication.getPrincipal();
    measurementService.deleteMeasurement(id, userId);
    return ResponseEntity.noContent().build();
  }
}
