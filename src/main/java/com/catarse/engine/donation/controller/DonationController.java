package com.catarse.engine.donation.controller;

import com.catarse.engine.donation.dto.request.DonationRequest;
import com.catarse.engine.donation.dto.response.DonationResponse;
import com.catarse.engine.donation.service.DonationService;
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
@RequestMapping("/api/v1/donations")
@RequiredArgsConstructor
@Tag(name = "Donations", description = "Donation management endpoints")
public class DonationController {

    private final DonationService donationService;

    @PostMapping
    @Operation(summary = "Create a new donation")
    public ResponseEntity<DonationResponse> createDonation(
            @Valid @RequestBody DonationRequest request,
            @RequestAttribute("userId") Long userId) {
        DonationResponse response = donationService.createDonation(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get donation by ID")
    public ResponseEntity<DonationResponse> getDonationById(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        DonationResponse response = donationService.getDonationById(id, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get donations by user ID")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Page<DonationResponse>> getDonationsByUser(
            @PathVariable Long userId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<DonationResponse> donations = donationService.getDonationsByUser(userId, pageable);
        return ResponseEntity.ok(donations);
    }

    @GetMapping("/campaign/{campaignId}")
    @Operation(summary = "Get donations by campaign ID")
    public ResponseEntity<Page<DonationResponse>> getDonationsByCampaign(
            @PathVariable Long campaignId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<DonationResponse> donations = donationService.getDonationsByCampaign(campaignId, pageable);
        return ResponseEntity.ok(donations);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update donation status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DonationResponse> updateDonationStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestAttribute("userId") Long userId) {
        DonationResponse response = donationService.updateDonationStatus(id, status, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "List all donations (admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<DonationResponse>> getAllDonations(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<DonationResponse> donations = donationService.getAllDonations(pageable);
        return ResponseEntity.ok(donations);
    }
}