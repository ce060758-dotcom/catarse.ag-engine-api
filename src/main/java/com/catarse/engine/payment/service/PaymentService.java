package com.catarse.engine.payment.service;

import com.catarse.engine.payment.dto.request.PaymentRequest;
import com.catarse.engine.payment.dto.response.PaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {

    // Processar um novo pagamento
    PaymentResponse processPayment(PaymentRequest request, Long userId);

    // Buscar pagamento por ID
    PaymentResponse getPaymentById(Long id, Long userId);

    // Listar pagamentos de um usuário
    Page<PaymentResponse> getPaymentsByUser(Long userId, Pageable pageable);

    // Listar pagamentos de uma doação
    Page<PaymentResponse> getPaymentsByDonation(Long donationId, Pageable pageable);

    // Atualizar status do pagamento (admin/webhook)
    PaymentResponse updatePaymentStatus(Long id, String status, Long userId);

    // Listar todos pagamentos (admin)
    Page<PaymentResponse> getAllPayments(Pageable pageable);
}