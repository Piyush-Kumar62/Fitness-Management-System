package com.project.fitness.domain.gym.service;

import com.project.fitness.domain.gym.dto.GymRequest;
import com.project.fitness.domain.gym.dto.GymResponse;
import com.project.fitness.common.exception.BadRequestException;
import com.project.fitness.common.exception.ResourceNotFoundException;
import com.project.fitness.domain.gym.model.Gym;
import com.project.fitness.domain.gym.model.GymStatus;
import com.project.fitness.domain.gym.repository.GymRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GymService {

  private final GymRepository gymRepository;

  public GymResponse createGym(String ownerId, GymRequest request) {
    String normalizedName = request.getName().trim();
    if (gymRepository.existsByNameIgnoreCase(normalizedName)) {
      throw new BadRequestException("Gym with this name already exists");
    }
    Gym gym = Gym.builder()
        .name(normalizedName)
        .ownerId(ownerId)
        .address(request.getAddress().trim())
        .contact(request.getContact().trim())
        .status(GymStatus.PENDING)
        .build();
    return toResponse(gymRepository.save(gym));
  }

  @Transactional(readOnly = true)
  public List<GymResponse> getAllGyms() {
    return gymRepository.findAll().stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public GymResponse getGymById(String gymId) {
    return toResponse(findGym(gymId));
  }

  @Transactional(readOnly = true)
  public List<GymResponse> getOwnerGyms(String ownerId) {
    return gymRepository.findByOwnerId(ownerId).stream().map(this::toResponse).toList();
  }

  public GymResponse updateGym(String gymId, GymRequest request) {
    Gym gym = findGym(gymId);
    gym.setName(request.getName().trim());
    gym.setAddress(request.getAddress().trim());
    gym.setContact(request.getContact().trim());
    return toResponse(gymRepository.save(gym));
  }

  public void deleteGym(String gymId) {
    gymRepository.delete(findGym(gymId));
  }

  private Gym findGym(String gymId) {
    return gymRepository.findById(gymId)
        .orElseThrow(() -> new ResourceNotFoundException("Gym", "id", gymId));
  }

  private GymResponse toResponse(Gym gym) {
    return GymResponse.builder()
        .id(gym.getId())
        .name(gym.getName())
        .ownerId(gym.getOwnerId())
        .address(gym.getAddress())
        .contact(gym.getContact())
        .status(gym.getStatus())
        .createdAt(gym.getCreatedAt())
        .updatedAt(gym.getUpdatedAt())
        .build();
  }
}
