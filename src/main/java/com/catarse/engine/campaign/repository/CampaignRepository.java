package com.catarse.engine.campaign.repository;

import com.catarse.engine.campaign.entity.Campaign;
import com.catarse.engine.campaign.entity.CampaignStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    // Buscar campanhas por usuário
    Page<Campaign> findByUserId(Long userId, Pageable pageable);

    // Buscar campanhas ativas
    Page<Campaign> findByStatus(CampaignStatus status, Pageable pageable);

    // Buscar campanhas por status e usuário
    Page<Campaign> findByUserIdAndStatus(Long userId, CampaignStatus status, Pageable pageable);

    // Verificar se usuário é dono da campanha
    boolean existsByIdAndUserId(Long id, Long userId);

    // Buscar campanhas que expiraram e ainda estão ativas (para job)
    List<Campaign> findByStatusAndEndDateBefore(CampaignStatus status, LocalDateTime date);

    // Buscar campanhas que atingiram meta (para job)
    @Query("SELECT c FROM Campaign c WHERE c.status = :status AND c.currentAmount >= c.goalAmount")
    List<Campaign> findCampaignsThatReachedGoal(@Param("status") CampaignStatus status);

    // Buscar campanha com validação de dono (para segurança)
    Optional<Campaign> findByIdAndUserId(Long id, Long userId);
}