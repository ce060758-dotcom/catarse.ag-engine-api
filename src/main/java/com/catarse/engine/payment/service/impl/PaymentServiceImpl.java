package com.catarse.engine.payment.service.impl;

import com.catarse.engine.donation.entity.Donation;
import com.catarse.engine.donation.entity.DonationStatus;
import com.catarse.engine.donation.repository.DonationRepository;
import com.catarse.engine.exception.BusinessException;
import com.catarse.engine.exception.ResourceNotFoundException;
import com.catarse.engine.payment.dto.request.PaymentRequest;
import com.catarse.engine.payment.dto.response.PaymentResponse;
import com.catarse.engine.payment.entity.PaymentEntity;
import com.catarse.engine.payment.entity.PaymentStatus;
import com.catarse.engine.payment.repository.PaymentRepository;
import com.catarse.engine.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final DonationRepository donationRepository;

    @Override
    @Transactional
    @CacheEvict(value = "payments", allEntries = true)
    public PaymentResponse processPayment(PaymentRequest request, Long userId) {
        // Verificar se a doação existe
        Donation donation = donationRepository.findById(request.getDonationId())
                .orElseThrow(() -> new ResourceNotFoundException("Donation not found with id: " + request.getDonationId()));

        // Validar se o usuário é o dono da doação
        if (!donation.getUserId().equals(userId)) {
            throw new BusinessException("You can only pay for your own donations");
        }

        // Validar se a doação já não foi paga
        if (donation.getStatus() == DonationStatus.COMPLETED) {
            throw new BusinessException("This donation has already been paid");
        }

        // Validar valor
        if (!donation.getAmount().equals(request.getAmount())) {
            throw new BusinessException("PaymentEntity amount must match donation amount");
        }

        PaymentEntity payment = new PaymentEntity();
        payment.setDonationId(request.getDonationId());
        payment.setUserId(userId);
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus(PaymentStatus.PROCESSING);

        // Simular processamento com gateway
        PaymentEntity processedPayment = simulateGatewayProcessing(payment, request);

        // Atualizar status da doação se pagamento foi aprovado
        if (processedPayment.getStatus() == PaymentStatus.APPROVED) {
            donation.setStatus(DonationStatus.COMPLETED);
            donation.setPaidAt(LocalDateTime.now());
            donationRepository.save(donation);
        }

        PaymentEntity savedPayment = paymentRepository.save(processedPayment);
        return mapToResponse(savedPayment);
    }

    @Override
    @Cacheable(value = "payments", key = "#id")
    public PaymentResponse getPaymentById(Long id, Long userId) {
        PaymentEntity payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentEntity not found with id: " + id));

        // Qualquer um pode ver? Apenas dono ou admin
        if (!payment.getUserId().equals(userId)) {
            throw new BusinessException("You don't have permission to view this payment");
        }

        return mapToResponse(payment);
    }

    @Override
    public Page<PaymentResponse> getPaymentsByUser(Long userId, Pageable pageable) {
        return paymentRepository.findByUserId(userId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public Page<PaymentResponse> getPaymentsByDonation(Long donationId, Pageable pageable) {
        return paymentRepository.findByDonationId(donationId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    @CacheEvict(value = "payments", key = "#id")
    public PaymentResponse updatePaymentStatus(Long id, String status, Long userId) {
        PaymentEntity payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PaymentEntity not found with id: " + id));

        // Apenas admin pode forçar status
        // TODO: Verificar role do usuário

        try {
            PaymentStatus newStatus = PaymentStatus.valueOf(status.toUpperCase());
            payment.setStatus(newStatus);

            if ((newStatus == PaymentStatus.APPROVED) || (newStatus == PaymentStatus.COMPLETED)) {
                payment.setPaidAt(LocalDateTime.now());

                // Atualizar doação
                Donation donation = donationRepository.findById(payment.getDonationId())
                        .orElseThrow(() -> new ResourceNotFoundException("Donation not found"));
                donation.setStatus(DonationStatus.COMPLETED);
                donation.setPaidAt(LocalDateTime.now());
                donationRepository.save(donation);
            }

        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid status: " + status);
        }

        PaymentEntity updatedPayment = paymentRepository.save(payment);
        return mapToResponse(updatedPayment);
    }

    @Override
    @Cacheable(value = "payments", key = "'all_' + #pageable.pageNumber")
    public Page<PaymentResponse> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    private PaymentEntity simulateGatewayProcessing(PaymentEntity payment, PaymentRequest request) {
        // Simulação de processamento com gateway de pagamento
        log.info("Processing payment for donation: {}", payment.getDonationId());

        // Gerar ID de transação
        payment.setTransactionId(UUID.randomUUID().toString());

        // Simular regras por método de pagamento
        switch (request.getPaymentMethod()) {
            case "CREDIT_CARD":
                // Validar cartão (simulação)
                if (request.getCardNumber() == null || request.getCardNumber().length() < 16) {
                    payment.setStatus(PaymentStatus.FAILED);
                    payment.setGatewayResponse("Invalid credit card");
                } else {
                    payment.setStatus(PaymentStatus.APPROVED);
                    payment.setGatewayResponse("PaymentEntity approved");
                    // Mascarar número do cartão para log
                    String maskedCard = "**** **** **** " +
                            request.getCardNumber().substring(request.getCardNumber().length() - 4);
                    log.info("Credit card payment approved: {}", maskedCard);
                }
                break;

            case "PIX":
                // PIX é geralmente instantâneo
                payment.setStatus(PaymentStatus.APPROVED);
                payment.setGatewayResponse("PIX payment approved");
                log.info("PIX payment approved");
                break;

            case "BOLETO":
                // Boleto leva tempo para compensar
                payment.setStatus(PaymentStatus.PENDING);
                payment.setGatewayResponse("Boleto generated, waiting payment");
                log.info("Boleto generated");
                break;

            default:
                payment.setStatus(PaymentStatus.FAILED);
                payment.setGatewayResponse("Unsupported payment method");
        }

        return payment;
    }

    private PaymentResponse mapToResponse(PaymentEntity payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setDonationId(payment.getDonationId());
        response.setUserId(payment.getUserId());
        response.setAmount(payment.getAmount());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setStatus(payment.getStatus().name());
        response.setTransactionId(payment.getTransactionId());
        response.setGatewayResponse(payment.getGatewayResponse());
        response.setPaidAt(payment.getPaidAt());
        response.setCreatedAt(payment.getCreatedAt());
        response.setUpdatedAt(payment.getUpdatedAt());
        return response;
    }
}