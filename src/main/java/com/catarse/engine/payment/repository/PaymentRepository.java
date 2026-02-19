package com.catarse.engine.payment.repository;


import com.catarse.engine.payment.entity.PaymentEntity;

import com.catarse.engine.payment.entity.PaymentEntity;
import com.catarse.engine.payment.entity.PaymentEntity;

import com.catarse.engine.payment.entity.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    Page<PaymentEntity> findByUserId(Long userId, Pageable pageable);

    Page<PaymentEntity> findByDonationId(Long donationId, Pageable pageable);

    Optional<PaymentEntity> findByTransactionId(String transactionId);

    List<PaymentEntity> findByStatusAndCreatedAtBefore(PaymentStatus status, LocalDateTime date);

    List<PaymentEntity> findByStatus(PaymentStatus status);

    boolean existsByDonationIdAndStatus(Long donationId, PaymentStatus status);
}