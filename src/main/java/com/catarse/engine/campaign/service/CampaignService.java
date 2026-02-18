package com.catarse.engine.campaign.service;

import com.catarse.engine.campaign.dto.request.CampaignRequest;
import com.catarse.engine.campaign.dto.response.CampaignResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CampaignService {

    CampaignResponse createCampaign(CampaignRequest request, Long userId);

    CampaignResponse getCampaignById(Long id, Long userId);

    Page<CampaignResponse> getAllActiveCampaigns(Pageable pageable);

    Page<CampaignResponse> getCampaignsByUser(Long userId, Pageable pageable);

    CampaignResponse updateCampaign(Long id, CampaignRequest request, Long userId);

    void deleteCampaign(Long id, Long userId);

    CampaignResponse updateCampaignStatus(Long id, String status, Long userId);
}