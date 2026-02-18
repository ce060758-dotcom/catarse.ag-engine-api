package com.catarse.engine.donation.service;

import com.catarse.engine.donation.dto.request.DonationRequest;
import com.catarse.engine.donation.dto.response.DonationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DonationService {

    DonationResponse createDonation(DonationRequest request, Long userId);

    DonationResponse getDonationById(Long id, Long userId);

    Page<DonationResponse> getDonationsByUser(Long userId, Pageable pageable);

    Page<DonationResponse> getDonationsByCampaign(Long campaignId, Pageable pageable);

    DonationResponse updateDonationStatus(Long id, String status, Long userId);

    Page<DonationResponse> getAllDonations(Pageable pageable); // Admin only
}