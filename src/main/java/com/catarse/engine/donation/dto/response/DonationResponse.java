package com.catarse.engine.donation.dto.response;

import com.catarse.engine.donation.entity.DonationStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DonationResponse {
    private Long id;
    private Long campaignId;
    private Long userId;
    private BigDecimal amount;
    private String paymentMethod;
    private DonationStatus status;
    private String transactionId;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
}