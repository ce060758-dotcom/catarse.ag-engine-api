package com.catarse.engine.donation.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "donations")
@Data
@NoArgsConstructor
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long campaignId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String paymentMethod;  // CREDIT_CARD, PIX, BOLETO

    @Enumerated(EnumType.STRING)
    private DonationStatus status = DonationStatus.PENDING;

    private String transactionId;

    private LocalDateTime paidAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
}

