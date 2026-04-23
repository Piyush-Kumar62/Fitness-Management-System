package com.project.fitness.mapper;

import com.project.fitness.domain.trainer.dto.ClassBookingResponse;
import com.project.fitness.domain.trainer.dto.ClassScheduleResponse;
import com.project.fitness.domain.trainer.model.ClassBooking;
import com.project.fitness.domain.trainer.model.ClassSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// MapStruct mapper: ClassSchedule and ClassBooking entities ↔ response DTOs.
@Mapper(componentModel = "spring")
public interface ClassScheduleMapper {

  // Map ClassSchedule to response DTO.
  @Mapping(target = "gymId", source = "gymId")
  @Mapping(target = "trainerName", ignore = true)
  @Mapping(target = "bookedCount", ignore = true)
  @Mapping(target = "availableSlots", ignore = true)
  ClassScheduleResponse toResponse(ClassSchedule schedule);

  // Map ClassBooking to response DTO. Names resolved by service.
  @Mapping(target = "className", ignore = true)
  @Mapping(target = "memberName", ignore = true)
  ClassBookingResponse toBookingResponse(ClassBooking booking);
}
