package com.catarse.engine.payment.controller;

import com.catarse.engine.payment.dto.request.PaymentRequest;
import com.catarse.engine.payment.dto.response.PaymentResponse;
import com.catarse.engine.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment processing endpoints")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    @Operation(summary = "Process a payment for a donation")
    public ResponseEntity<PaymentResponse> processPayment(
            @Valid @RequestBody PaymentRequest request,
            @RequestAttribute("userId") Long userId) {
        PaymentResponse response = paymentService.processPayment(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID")
    public ResponseEntity<PaymentResponse> getPaymentById(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        PaymentResponse response = paymentService.getPaymentById(id, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get payments by user ID")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Page<PaymentResponse>> getPaymentsByUser(
            @PathVariable Long userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PaymentResponse> payments = paymentService.getPaymentsByUser(userId, pageable);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/donation/{donationId}")
    @Operation(summary = "Get payments by donation ID")
    public ResponseEntity<Page<PaymentResponse>> getPaymentsByDonation(
            @PathVariable Long donationId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<PaymentResponse> payments = paymentService.getPaymentsByDonation(donationId, pageable);
        return ResponseEntity.ok(payments);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update payment status (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentResponse> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestAttribute("userId") Long userId) {
        PaymentResponse response = paymentService.updatePaymentStatus(id, status, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "List all payments (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PaymentResponse>> getAllPayments(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<PaymentResponse> payments = paymentService.getAllPayments(pageable);
        return ResponseEntity.ok(payments);
    }
}