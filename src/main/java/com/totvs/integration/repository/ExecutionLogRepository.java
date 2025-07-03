
package com.totvs.integration.repository;

import com.totvs.integration.entity.ExecutionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExecutionLogRepository extends JpaRepository<ExecutionLog, String> {
    
    List<ExecutionLog> findByTenantIdOrderByCreatedAtDesc(String tenantId);
    Page<ExecutionLog> findByTenantIdOrderByCreatedAtDesc(String tenantId, Pageable pageable);
    
    List<ExecutionLog> findByIntegrationIdOrderByCreatedAtDesc(String integrationId);
    Page<ExecutionLog> findByIntegrationIdOrderByCreatedAtDesc(String integrationId, Pageable pageable);
    
    List<ExecutionLog> findByTenantIdAndStatus(String tenantId, ExecutionLog.ExecutionStatus status);
    
    @Query("SELECT e FROM ExecutionLog e WHERE e.tenantId = :tenantId " +
           "AND e.createdAt >= :startDate AND e.createdAt <= :endDate")
    List<ExecutionLog> findByTenantIdAndDateRange(@Param("tenantId") String tenantId, 
                                                 @Param("startDate") LocalDateTime startDate, 
                                                 @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT e FROM ExecutionLog e WHERE e.integrationId = :integrationId " +
           "AND e.createdAt >= :startDate AND e.createdAt <= :endDate")
    List<ExecutionLog> findByIntegrationIdAndDateRange(@Param("integrationId") String integrationId, 
                                                      @Param("startDate") LocalDateTime startDate, 
                                                      @Param("endDate") LocalDateTime endDate);
    
    long countByTenantIdAndStatus(String tenantId, ExecutionLog.ExecutionStatus status);
    long countByIntegrationIdAndStatus(String integrationId, ExecutionLog.ExecutionStatus status);
    
    @Query("SELECT COUNT(e) FROM ExecutionLog e WHERE e.tenantId = :tenantId " +
           "AND e.status = 'SUCCESS' AND e.createdAt >= :since")
    long countSuccessfulExecutionsSince(@Param("tenantId") String tenantId, 
                                       @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(e) FROM ExecutionLog e WHERE e.tenantId = :tenantId " +
           "AND e.createdAt >= :since")
    long countExecutionsSince(@Param("tenantId") String tenantId, 
                             @Param("since") LocalDateTime since);
    
    @Query("SELECT e FROM ExecutionLog e WHERE e.tenantId = :tenantId " +
           "AND e.status = 'SUCCESS' ORDER BY e.createdAt DESC")
    List<ExecutionLog> findLatestSuccessfulExecutions(@Param("tenantId") String tenantId, 
                                                     Pageable pageable);
}