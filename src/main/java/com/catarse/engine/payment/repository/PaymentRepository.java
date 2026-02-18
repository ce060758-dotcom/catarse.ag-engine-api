package com.catarse.engine.payment;

<<<<<<< HEAD
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long donationId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(nullable = false, unique = true)
    private String transactionId;

    @Column(length = 50)
    private String paymentMethod; // CREDIT_CARD, PIX, BOLETO

    @CreationTimestamp
    private LocalDateTime paymentDate;

    private LocalDateTime confirmedAt;
=======
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
>>>>>>> feat/payment
}