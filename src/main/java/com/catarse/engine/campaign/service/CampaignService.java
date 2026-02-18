package com.catarse.engine.campaign.service;

import com.catarse.engine.campaign.dto.request.CampaignRequest;
import com.catarse.engine.campaign.dto.response.CampaignResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CampaignService {

    CampaignResponse createCampaign(CampaignRequest request, Long userId);  // ✓ OK

    CampaignResponse getCampaignById(Long id, Long userId);  // ✓ OK

    Page<CampaignResponse> getAllActiveCampaigns(Pageable pageable);  // ✓ OK

    Page<CampaignResponse> getCampaignsByUser(Long userId, Pageable pageable);  // ✓ OK

    CampaignResponse updateCampaign(Long id, CampaignRequest request, Long userId);  // ✓ OK

    void deleteCampaign(Long id, Long userId);  // ✓ OK

    CampaignResponse updateCampaignStatus(Long id, String status, Long userId);  // ✓ OK
}