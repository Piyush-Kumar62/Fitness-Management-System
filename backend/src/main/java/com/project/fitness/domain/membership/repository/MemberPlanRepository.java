package com.project.fitness.domain.membership.repository;
import com.project.fitness.domain.membership.model.Membership;

import com.project.fitness.domain.membership.model.MemberPlan;
import com.project.fitness.domain.membership.model.MemberPlan.PlanStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberPlanRepository extends JpaRepository<MemberPlan, String> {

  List<MemberPlan> findByMemberId(String memberId);

  List<MemberPlan> findByMemberIdAndStatus(String memberId, PlanStatus status);

  List<MemberPlan> findByAssignedBy(String trainerId);

  Optional<MemberPlan> findFirstByMemberIdAndStatusOrderByAssignedAtDesc(String memberId, PlanStatus status);

  long countByAssignedBy(String trainerId);

  long countByAssignedByAndStatus(String trainerId, PlanStatus status);
}
