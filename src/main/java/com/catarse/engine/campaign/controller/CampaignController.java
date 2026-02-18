package com.catarse.engine.campaign.controller;

import com.catarse.engine.campaign.dto.request.CampaignRequest;
import com.catarse.engine.campaign.dto.response.CampaignResponse;
import com.catarse.engine.campaign.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;  // ← Tem que estar aqui!

@RestController
@RequestMapping("/api/v1/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;

    @PostMapping
    public ResponseEntity<CampaignResponse> createCampaign(
            @Valid @RequestBody CampaignRequest request,
            @RequestAttribute("userId") Long userId) {
        CampaignResponse response = campaignService.createCampaign(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampaignResponse> getCampaignById(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        CampaignResponse response = campaignService.getCampaignById(id, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<CampaignResponse>> getAllActiveCampaigns(Pageable pageable) {
        Page<CampaignResponse> campaigns = campaignService.getAllActiveCampaigns(pageable);
        return ResponseEntity.ok(campaigns);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<CampaignResponse>> getCampaignsByUser(
            @PathVariable Long userId,
            Pageable pageable) {
        Page<CampaignResponse> campaigns = campaignService.getCampaignsByUser(userId, pageable);
        return ResponseEntity.ok(campaigns);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CampaignResponse> updateCampaign(
            @PathVariable Long id,
            @Valid @RequestBody CampaignRequest request,
            @RequestAttribute("userId") Long userId) {
        CampaignResponse response = campaignService.updateCampaign(id, request, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCampaign(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        campaignService.deleteCampaign(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CampaignResponse> updateCampaignStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestAttribute("userId") Long userId) {
        CampaignResponse response = campaignService.updateCampaignStatus(id, status, userId);
        return ResponseEntity.ok(response);
    }
}