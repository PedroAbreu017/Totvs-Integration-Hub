package com.totvs.integration.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tenant_stats", indexes = {
    @Index(name = "idx_tenant_stats_tenant", columnList = "tenant_id"),
    @Index(name = "idx_tenant_stats_updated", columnList = "updated_at")
})
public class TenantStats {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(name = "tenant_id", unique = true, nullable = false)
    private String tenantId;
    
    
    @Column(name = "total_integrations")
    private Long totalIntegrations = 0L;
    
    @Column(name = "active_integrations")
    private Long activeIntegrations = 0L;
    
    @Column(name = "total_executions")
    private Long totalExecutions = 0L;
    
    @Column(name = "successful_executions")
    private Long successfulExecutions = 0L;
    
    @Column(name = "failed_executions")
    private Long failedExecutions = 0L;
    
    
    @Column(name = "current_month_executions")
    private Long currentMonthExecutions = 0L;
    
    @Column(name = "current_month_data_transferred")
    private Long currentMonthDataTransferred = 0L; 
    
    @Column(name = "current_month_api_calls")
    private Long currentMonthApiCalls = 0L;
    
   
    @Column(name = "average_execution_time_ms")
    private Double averageExecutionTimeMs = 0.0;
    
    @Column(name = "success_rate")
    private Double successRate = 0.0; 
    
    
    @Column(name = "current_minute_requests")
    private Long currentMinuteRequests = 0L;
    
    @Column(name = "current_hour_requests")
    private Long currentHourRequests = 0L;
    
    @Column(name = "current_day_requests")
    private Long currentDayRequests = 0L;
    
    
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "connector_usage", columnDefinition = "TEXT")
    private Map<String, Long> connectorUsage; // connector type -> usage count
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "monthly_usage_by_day", columnDefinition = "TEXT")
    private Map<String, Long> monthlyUsageByDay;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
   
    public void incrementTotalExecutions() {
        this.totalExecutions = (this.totalExecutions == null ? 0L : this.totalExecutions) + 1;
        this.currentMonthExecutions = (this.currentMonthExecutions == null ? 0L : this.currentMonthExecutions) + 1;
    }
    
    public void incrementSuccessfulExecutions() {
        this.successfulExecutions = (this.successfulExecutions == null ? 0L : this.successfulExecutions) + 1;
        updateSuccessRate();
    }
    
    public void incrementFailedExecutions() {
        this.failedExecutions = (this.failedExecutions == null ? 0L : this.failedExecutions) + 1;
        updateSuccessRate();
    }
    
    private void updateSuccessRate() {
        if (totalExecutions != null && totalExecutions > 0 && successfulExecutions != null) {
            this.successRate = (successfulExecutions.doubleValue() / totalExecutions.doubleValue()) * 100.0;
        }
    }
}