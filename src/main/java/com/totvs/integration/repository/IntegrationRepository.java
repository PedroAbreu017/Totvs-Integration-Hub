package com.totvs.integration.repository;

import com.totvs.integration.entity.Integration;
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
public interface IntegrationRepository extends JpaRepository<Integration, String> {
    
    
    Page<Integration> findByTenantIdAndStatus(String tenantId, Integration.IntegrationStatus status, Pageable pageable);
    
    
    @Query("SELECT i FROM Integration i WHERE i.tenantId = :tenantId " +
           "AND LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Integration> findByTenantIdAndNameContaining(
        @Param("tenantId") String tenantId,
        @Param("name") String name, 
        Pageable pageable
    );
    
    
    @Query(value = """
        SELECT i.* FROM integrations i 
        WHERE i.tenant_id = :tenantId 
        AND (
            i.tags IS NOT NULL 
            AND (
                i.tags LIKE CONCAT('%"', :tag1, '"%')
                OR (:tag2 IS NOT NULL AND i.tags LIKE CONCAT('%"', :tag2, '"%'))
                OR (:tag3 IS NOT NULL AND i.tags LIKE CONCAT('%"', :tag3, '"%'))
                OR (:tag4 IS NOT NULL AND i.tags LIKE CONCAT('%"', :tag4, '"%'))
                OR (:tag5 IS NOT NULL AND i.tags LIKE CONCAT('%"', :tag5, '"%'))
            )
        )
        """, 
        countQuery = """
        SELECT COUNT(*) FROM integrations i 
        WHERE i.tenant_id = :tenantId 
        AND (
            i.tags IS NOT NULL 
            AND (
                i.tags LIKE CONCAT('%"', :tag1, '"%')
                OR (:tag2 IS NOT NULL AND i.tags LIKE CONCAT('%"', :tag2, '"%'))
                OR (:tag3 IS NOT NULL AND i.tags LIKE CONCAT('%"', :tag3, '"%'))
                OR (:tag4 IS NOT NULL AND i.tags LIKE CONCAT('%"', :tag4, '"%'))
                OR (:tag5 IS NOT NULL AND i.tags LIKE CONCAT('%"', :tag5, '"%'))
            )
        )
        """,
        nativeQuery = true)
    Page<Integration> findByTenantIdAndTagsIn(
        @Param("tenantId") String tenantId,
        @Param("tag1") String tag1,
        @Param("tag2") String tag2,
        @Param("tag3") String tag3,
        @Param("tag4") String tag4,
        @Param("tag5") String tag5,
        Pageable pageable
    );

    
    Page<Integration> findByTenantId(String tenantId, Pageable pageable);
    
    
    Optional<Integration> findByIdAndTenantId(String id, String tenantId);
    
    
    List<Integration> findByTenantId(String tenantId);
    
    List<Integration> findByTenantIdAndStatus(String tenantId, Integration.IntegrationStatus status);
    
    @Query("SELECT i FROM Integration i WHERE i.tenantId = :tenantId " +
           "AND LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Integration> findByTenantIdAndNameContaining(
        @Param("tenantId") String tenantId,
        @Param("name") String name
    );
    
    
  
    @Query("SELECT i FROM Integration i WHERE i.status = 'ACTIVE' " +
           "AND (i.nextExecution IS NULL OR i.nextExecution <= :now)")
    List<Integration> findIntegrationsToExecute(@Param("now") LocalDateTime now);
    
    
    @Query("SELECT i FROM Integration i WHERE i.tenantId = :tenantId AND i.status = 'ACTIVE'")
    List<Integration> findActiveByTenantId(@Param("tenantId") String tenantId);
    
   
    
    long countByTenantId(String tenantId);
    
    long countByTenantIdAndStatus(String tenantId, Integration.IntegrationStatus status);
    
    @Query("SELECT COUNT(i) FROM Integration i WHERE i.tenantId = :tenantId AND i.status = 'ACTIVE'")
    long countActiveByTenantId(@Param("tenantId") String tenantId);
    
   

    
    Page<Integration> findByTenantIdAndCreatedAtBetween(
        String tenantId, 
        LocalDateTime startDate, 
        LocalDateTime endDate, 
        Pageable pageable
    );
    
    @Query("SELECT i FROM Integration i WHERE i.tenantId = :tenantId ORDER BY i.createdAt DESC")
    Page<Integration> findRecentByTenantId(@Param("tenantId") String tenantId, Pageable pageable);
    
    

    
    @Query("SELECT i FROM Integration i WHERE i.tenantId = :tenantId AND i.errorCount > 0 ORDER BY i.lastExecution DESC")
    List<Integration> findWithRecentErrors(@Param("tenantId") String tenantId);
    
    @Query("SELECT i FROM Integration i WHERE i.tenantId = :tenantId AND i.status = 'ERROR'")
    List<Integration> findFailedIntegrations(@Param("tenantId") String tenantId);
}