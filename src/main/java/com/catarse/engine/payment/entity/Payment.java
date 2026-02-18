package com.catarse.engine.payment.entity;  // ← CORRETO

public enum Payment {
    PENDING,
    PROCESSING,
    APPROVED,
    FAILED,
    REFUNDED,
    CANCELLED
}