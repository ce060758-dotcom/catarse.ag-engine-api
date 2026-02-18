package com.catarse.engine.payment.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {

    @NotNull(message = "Donation ID is required")
    private Long donationId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", message = "Amount must be at least R$ 1,00")
    private BigDecimal amount;

    @NotNull(message = "Payment method is required")
    @Pattern(regexp = "CREDIT_CARD|PIX|BOLETO", message = "Payment method must be CREDIT_CARD, PIX or BOLETO")
    private String paymentMethod;

    private String cardNumber;      // Para CREDIT_CARD
    private String cardHolderName;  // Para CREDIT_CARD
    private String cardExpiry;      // Para CREDIT_CARD
    private String cardCvv;         // Para CREDIT_CARD
    private String pixKey;          // Para PIX
    private String boletoNumber;    // Para BOLETO
}