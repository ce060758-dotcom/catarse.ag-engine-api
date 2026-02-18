package com.catarse.engine.campaign.service.impl;

import com.catarse.engine.campaign.dto.request.CampaignRequest;
import com.catarse.engine.campaign.dto.response.CampaignResponse;
import com.catarse.engine.campaign.entity.Campaign;
import com.catarse.engine.campaign.entity.CampaignStatus;
import com.catarse.engine.campaign.repository.CampaignRepository;
import com.catarse.engine.campaign.service.CampaignService;
import com.catarse.engine.exception.ResourceNotFoundException;
import com.catarse.engine.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {

    private final CampaignRepository campaignRepository;

    @Override
    @Transactional
    @CacheEvict(value = "campaigns", allEntries = true)
    public CampaignResponse createCampaign(CampaignRequest request, Long userId) {
        // Validação de datas
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        Campaign campaign = new Campaign();
        campaign.setTitle(request.getTitle());
        campaign.setDescription(request.getDescription());
        campaign.setGoalAmount(request.getGoalAmount());
        campaign.setStartDate(request.getStartDate());
        campaign.setEndDate(request.getEndDate());
        campaign.setUserId(userId);
        campaign.setCurrentAmount(java.math.BigDecimal.ZERO);
        campaign.setStatus(CampaignStatus.DRAFT);

        Campaign savedCampaign = campaignRepository.save(campaign);
        return mapToResponse(savedCampaign);
    }

    @Override
    @Cacheable(value = "campaigns", key = "#id")
    public CampaignResponse getCampaignById(Long id, Long userId) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + id));

        // Qualquer um pode ver campanha, mas só o dono vê informações completas?
        // Por enquanto, qualquer um vê (ajustaremos depois com roles)
        return mapToResponse(campaign);
    }

    @Override
    @Cacheable(value = "campaigns", key = "'active_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<CampaignResponse> getAllActiveCampaigns(Pageable pageable) {
        return campaignRepository.findByStatus(CampaignStatus.ACTIVE, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public Page<CampaignResponse> getCampaignsByUser(Long userId, Pageable pageable) {
        return campaignRepository.findByUserId(userId, pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    @CacheEvict(value = "campaigns", allEntries = true)
    public CampaignResponse updateCampaign(Long id, CampaignRequest request, Long userId) {
        Campaign campaign = findCampaignAndValidateOwner(id, userId);

        // Validação de datas
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date");
        }

        campaign.setTitle(request.getTitle());
        campaign.setDescription(request.getDescription());
        campaign.setGoalAmount(request.getGoalAmount());
        campaign.setStartDate(request.getStartDate());
        campaign.setEndDate(request.getEndDate());

        Campaign updatedCampaign = campaignRepository.save(campaign);
        return mapToResponse(updatedCampaign);
    }

    @Override
    @Transactional
    @CacheEvict(value = "campaigns", allEntries = true)
    public void deleteCampaign(Long id, Long userId) {
        Campaign campaign = findCampaignAndValidateOwner(id, userId);
        campaignRepository.delete(campaign);
    }

    @Override
    @Transactional
    @CacheEvict(value = "campaigns", allEntries = true)
    public CampaignResponse updateCampaignStatus(Long id, String status, Long userId) {
        Campaign campaign = findCampaignAndValidateOwner(id, userId);

        try {
            CampaignStatus newStatus = CampaignStatus.valueOf(status.toUpperCase());
            campaign.setStatus(newStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }

        Campaign updatedCampaign = campaignRepository.save(campaign);
        return mapToResponse(updatedCampaign);
    }

    private Campaign findCampaignAndValidateOwner(Long id, Long userId) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + id));

        if (!campaign.getUserId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to modify this campaign");
        }

        return campaign;
    }

    private CampaignResponse mapToResponse(Campaign campaign) {
        CampaignResponse response = new CampaignResponse();
        response.setId(campaign.getId());
        response.setTitle(campaign.getTitle());
        response.setDescription(campaign.getDescription());
        response.setGoalAmount(campaign.getGoalAmount());
        response.setCurrentAmount(campaign.getCurrentAmount());
        response.setStartDate(campaign.getStartDate());
        response.setEndDate(campaign.getEndDate());
        response.setStatus(campaign.getStatus());
        response.setUserId(campaign.getUserId());
        response.setCreatedAt(campaign.getCreatedAt());
        response.setUpdatedAt(campaign.getUpdatedAt());
        return response;
    }
}