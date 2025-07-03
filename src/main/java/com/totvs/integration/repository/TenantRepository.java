
package com.totvs.integration.repository;

import com.totvs.integration.entity.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, String> {
    
    Optional<Tenant> findByDomain(String domain);
    Optional<Tenant> findByTenantId(String tenantId);
    Optional<Tenant> findByApiKey(String apiKey);
    
    List<Tenant> findByStatus(Tenant.Status status);
    Page<Tenant> findByStatus(Tenant.Status status, Pageable pageable);
    
    List<Tenant> findByPlan(Tenant.Plan plan);
    Page<Tenant> findByPlan(Tenant.Plan plan, Pageable pageable);
    
    boolean existsByDomain(String domain);
    boolean existsByTenantId(String tenantId);
    boolean existsByApiKey(String apiKey);
    
    List<Tenant> findByEmail(String email);
    
    List<Tenant> findByPlanAndStatus(Tenant.Plan plan, Tenant.Status status);
    Page<Tenant> findByPlanAndStatus(Tenant.Plan plan, Tenant.Status status, Pageable pageable);
    
    @Query("SELECT t FROM Tenant t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Tenant> findByNameContainingIgnoreCase(@Param("name") String name);
    
    @Query("SELECT t FROM Tenant t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(t.domain) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Tenant> searchByNameOrDomain(@Param("searchTerm") String searchTerm, Pageable pageable);
}