package com.project.fitness.mapper;

import com.project.fitness.domain.membership.dto.MembershipPlanResponse;
import com.project.fitness.domain.membership.dto.MembershipResponse;
import com.project.fitness.domain.membership.model.Membership;
import com.project.fitness.domain.membership.model.MembershipPlan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// MapStruct mapper: Membership and MembershipPlan entities ↔ response DTOs.
@Mapper(componentModel = "spring")
public interface MembershipMapper {

  // Map Membership entity to response. planName and memberName are resolved by service.
  @Mapping(target = "planName", ignore = true)
  @Mapping(target = "memberName", ignore = true)
  MembershipResponse toResponse(Membership membership);

  // Map MembershipPlan entity to response DTO.
  MembershipPlanResponse toPlanResponse(MembershipPlan plan);
}
