package com.totvs.integration.repository;

import com.totvs.integration.entity.TenantStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantStatsRepository extends JpaRepository<TenantStats, String> {
    
    Optional<TenantStats> findByTenantId(String tenantId);
    boolean existsByTenantId(String tenantId);
}