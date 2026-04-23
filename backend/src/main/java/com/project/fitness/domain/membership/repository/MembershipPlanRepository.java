package com.project.fitness.domain.membership.repository;
import com.project.fitness.domain.membership.model.Membership;

import com.project.fitness.domain.membership.model.MembershipPlan;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, String> {

  List<MembershipPlan> findByGymIdAndActiveTrue(String gymId);

  List<MembershipPlan> findByGymId(String gymId);
}
