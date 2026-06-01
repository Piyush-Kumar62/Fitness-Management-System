package com.project.fitness.domain.membership.repository;

import com.project.fitness.domain.membership.model.Membership;
import com.project.fitness.domain.membership.model.MembershipStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, String> {

  List<Membership> findByMemberIdOrderByCreatedAtDesc(String memberId);
  
  Page<Membership> findByMemberIdOrderByCreatedAtDesc(String memberId, Pageable pageable);

  Membership findFirstByMemberIdAndStatusOrderByEndDateDesc(String memberId, MembershipStatus status);

  List<Membership> findByMemberIdInAndStatus(List<String> memberIds, MembershipStatus status);

  List<Membership> findByStatusAndEndDate(MembershipStatus status, java.time.LocalDate endDate);

  long countByStatus(MembershipStatus status);

  boolean existsByMemberIdAndStatus(String memberId, MembershipStatus status);
}
