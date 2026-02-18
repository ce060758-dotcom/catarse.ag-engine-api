package com.catarse.engine.donation.repository;

import com.catarse.engine.donation.entity.Donation;
import com.catarse.engine.donation.entity.DonationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

    Page<Donation> findByUserId(Long userId, Pageable pageable);

    Page<Donation> findByCampaignId(Long campaignId, Pageable pageable);

    List<Donation> findByCampaignIdAndStatus(Long campaignId, DonationStatus status);

    boolean existsByIdAndUserId(Long id, Long userId);
}