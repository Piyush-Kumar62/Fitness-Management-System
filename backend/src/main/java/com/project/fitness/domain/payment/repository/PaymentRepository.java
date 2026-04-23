package com.project.fitness.domain.payment.repository;

import com.project.fitness.domain.payment.model.Payment;
import com.project.fitness.domain.payment.model.PaymentStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

  List<Payment> findByMemberIdOrderByCreatedAtDesc(String memberId);
  
  Page<Payment> findByMemberIdOrderByCreatedAtDesc(String memberId, Pageable pageable);

  List<Payment> findByStatusAndCreatedAtBetween(PaymentStatus status, LocalDateTime start, LocalDateTime end);

  List<Payment> findByMemberIdInAndStatusAndCreatedAtBetween(
      List<String> memberIds, PaymentStatus status, LocalDateTime start, LocalDateTime end);
}
