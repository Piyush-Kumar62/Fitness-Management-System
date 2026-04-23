package com.project.fitness.domain.gym.controller;
import com.project.fitness.domain.gym.dto.GymRequest;
import com.project.fitness.domain.gym.dto.GymResponse;
import com.project.fitness.modules.gym.application.GymApplicationService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/gyms")
@RequiredArgsConstructor
public class GymController {

  private final GymApplicationService gymApplicationService;

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
  public ResponseEntity<GymResponse> createGym(
      Authentication authentication,
      @Valid @RequestBody GymRequest request) {
    return ResponseEntity.ok(gymApplicationService.createGym((String) authentication.getPrincipal(), request));
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
  public ResponseEntity<List<GymResponse>> getAllGyms() {
    return ResponseEntity.ok(gymApplicationService.getAllGyms());
  }

  @GetMapping("/{gymId}")
  @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
  public ResponseEntity<GymResponse> getGymById(@PathVariable String gymId) {
    return ResponseEntity.ok(gymApplicationService.getGymById(gymId));
  }

  @GetMapping("/my")
  @PreAuthorize("hasRole('OWNER')")
  public ResponseEntity<List<GymResponse>> getOwnerGyms(Authentication authentication) {
    return ResponseEntity.ok(gymApplicationService.getOwnerGyms((String) authentication.getPrincipal()));
  }

  @PutMapping("/{gymId}")
  @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
  public ResponseEntity<GymResponse> updateGym(
      @PathVariable String gymId,
      @Valid @RequestBody GymRequest request) {
    return ResponseEntity.ok(gymApplicationService.updateGym(gymId, request));
  }

  @DeleteMapping("/{gymId}")
  @PreAuthorize("hasAnyRole('ADMIN','OWNER')")
  public ResponseEntity<Void> deleteGym(@PathVariable String gymId) {
    gymApplicationService.deleteGym(gymId);
    return ResponseEntity.noContent().build();
  }
}
