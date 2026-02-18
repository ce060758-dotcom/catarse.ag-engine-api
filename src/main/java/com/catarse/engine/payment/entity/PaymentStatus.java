package com.catarse.engine.payment.entity;

public enum PaymentStatus {
    PENDING,
    PROCESSING,
    APPROVED,
    FAILED,
    REFUNDED,
    CANCELLED,
    COMPLETED, CHARGEBACK
}