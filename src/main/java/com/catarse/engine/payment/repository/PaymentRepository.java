package com.catarse.engine.payment.repository;

import com.catarse.engine.payment.entity.Payment;
import com.catarse.engine.payment.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Page<Payment> findByUserId(Long userId, Pageable pageable);

    Page<Payment> findByDonationId(Long donationId, Pageable pageable);

    Optional<Payment> findByTransactionId(String transactionId);

    List<Payment> findByStatusAndCreatedAtBefore(PaymentStatus status, LocalDateTime date);

    List<Payment> findByStatus(PaymentStatus status);

    boolean existsByDonationIdAndStatus(Long donationId, PaymentStatus status);
}