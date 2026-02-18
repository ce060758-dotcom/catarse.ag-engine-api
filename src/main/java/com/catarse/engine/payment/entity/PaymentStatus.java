package com.catarse.engine.payment.entity;  // ← CORRETO

public enum PaymentStatus {
    PENDING,
    PROCESSING,
    APPROVED,
    FAILED,
    REFUNDED,
    CANCELLED
}