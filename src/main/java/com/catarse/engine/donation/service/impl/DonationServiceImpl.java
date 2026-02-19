package com.catarse.engine.donation.service.impl;

import com.catarse.engine.campaign.entity.Campaign;
import com.catarse.engine.campaign.repository.CampaignRepository;
import com.catarse.engine.donation.dto.request.DonationRequest;
import com.catarse.engine.donation.dto.response.DonationResponse;
import com.catarse.engine.donation.entity.Donation;
import com.catarse.engine.donation.entity.DonationStatus;
import com.catarse.engine.donation.repository.DonationRepository;
import com.catarse.engine.donation.service.DonationService;
import com.catarse.engine.exception.BusinessException;
import com.catarse.engine.exception.ResourceNotFoundException;
import com.catarse.engine.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DonationServiceImpl implements DonationService {

    private final DonationRepository donationRepository;
    private final CampaignRepository campaignRepository;

    @Override
    @Transactional
    @CacheEvict(value = {"donations", "campaigns"}, allEntries = true)
    public DonationResponse createDonation(DonationRequest request, Long userId) {
        // Validar valor mínimo
        if (request.getAmount().doubleValue() < 1.0) {
            throw new BusinessException("Donation amount must be at least R$ 1,00");
        }

        // Verificar se a campanha existe e está ativa
        Campaign campaign = campaignRepository.findById(request.getCampaignId())
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + request.getCampaignId()));

        if (campaign.getStatus() != com.catarse.engine.campaign.entity.CampaignStatus.ACTIVE) {
            throw new BusinessException("Cannot donate to an inactive campaign");
        }

        if (campaign.getEndDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Campaign has already ended");
        }

        Donation donation = new Donation();
        donation.setCampaignId(request.getCampaignId());
        donation.setUserId(userId);
        donation.setAmount(request.getAmount());
        donation.setPaymentMethod(request.getPaymentMethod());
        donation.setStatus(DonationStatus.PENDING);

        Donation savedDonation = donationRepository.save(donation);
        return mapToResponse(savedDonation);
    }

    @Override
    @Cacheable(value = "donations", key = "#id")
    public DonationResponse getDonationById(Long id, Long userId) {
        Donation donation = findDonationAndValidateAccess(id, userId, false);
        return mapToResponse(donation);
    }

    @Override
    public Page<DonationResponse> getDonationsByUser(Long userId, Pageable pageable) {
        return donationRepository.findByUserId(userId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public Page<DonationResponse> getDonationsByCampaign(Long campaignId, Pageable pageable) {
        return donationRepository.findByCampaignId(campaignId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"donations", "campaigns"}, key = "#id")
    public DonationResponse updateDonationStatus(Long id, String status, Long userId) {
        Donation donation = donationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donation not found with id: " + id));

        try {
            DonationStatus newStatus = DonationStatus.valueOf(status.toUpperCase());
            DonationStatus oldStatus = donation.getStatus();

            donation.setStatus(newStatus);

            if (newStatus == DonationStatus.COMPLETED && oldStatus != DonationStatus.COMPLETED) {
                donation.setPaidAt(LocalDateTime.now());

                // Atualizar o valor arrecadado na campanha
                Campaign campaign = campaignRepository.findById(donation.getCampaignId())
                        .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));

                BigDecimal newCurrentAmount = campaign.getCurrentAmount().add(donation.getAmount());
                campaign.setCurrentAmount(newCurrentAmount);

                // Verificar se a campanha atingiu a meta
                if (newCurrentAmount.compareTo(campaign.getGoalAmount()) >= 0) {
                    campaign.setStatus(com.catarse.engine.campaign.entity.CampaignStatus.COMPLETED);
                }

                campaignRepository.save(campaign);
            }

            if (newStatus == DonationStatus.REFUNDED || newStatus == DonationStatus.CANCELLED) {
                // Se for reembolso/cancelamento, diminuir o valor da campanha
                if (oldStatus == DonationStatus.COMPLETED) {
                    Campaign campaign = campaignRepository.findById(donation.getCampaignId())
                            .orElseThrow(() -> new ResourceNotFoundException("Campaign not found"));

                    BigDecimal newCurrentAmount = campaign.getCurrentAmount().subtract(donation.getAmount());
                    campaign.setCurrentAmount(newCurrentAmount);
                    campaignRepository.save(campaign);
                }
            }

        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid status: " + status);
        }

        Donation updatedDonation = donationRepository.save(donation);
        return mapToResponse(updatedDonation);
    }

    @Override
    @Cacheable(value = "donations", key = "'all_' + #pageable.pageNumber")
    public Page<DonationResponse> getAllDonations(Pageable pageable) {
        return donationRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    private Donation findDonationAndValidateAccess(Long id, Long userId, boolean requireOwner) {
        Donation donation = donationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donation not found with id: " + id));

        if (requireOwner && !donation.getUserId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to access this donation");
        }

        return donation;
    }

    private DonationResponse mapToResponse(Donation donation) {
        DonationResponse response = new DonationResponse();
        response.setId(donation.getId());
        response.setCampaignId(donation.getCampaignId());
        response.setUserId(donation.getUserId());
        response.setAmount(donation.getAmount());
        response.setPaymentMethod(donation.getPaymentMethod());
        response.setStatus(DonationStatus.valueOf(donation.getStatus().name()));  // ← CORRIGIDO: .name() em vez de valueOf()
        response.setTransactionId(donation.getTransactionId());
        response.setPaidAt(donation.getPaidAt());
        response.setCreatedAt(donation.getCreatedAt());
        return response;
    }
}