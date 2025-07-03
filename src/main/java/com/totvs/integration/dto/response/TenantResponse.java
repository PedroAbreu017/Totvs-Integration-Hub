package com.totvs.integration.dto.response;

import com.totvs.integration.entity.Tenant;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantResponse {
    
    private String id;
    private String name;
    private String domain;
    private String email;
    private Tenant.Status status;
    private Tenant.Plan plan;
    private String apiKey;
    private Map<String, Object> settings;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    
    private String tenantId;
    
    
    public String getTenantId() {
        return tenantId != null ? tenantId : domain;
    }
    
  
    public static TenantResponse fromTenant(Tenant tenant) {
        return TenantResponse.builder()
                .id(tenant.getId())
                .name(tenant.getName())
                .domain(tenant.getDomain())
                .email(tenant.getEmail())
                .status(tenant.getStatus())
                .plan(tenant.getPlan())
                .apiKey(tenant.getApiKey())
                .settings(tenant.getSettings())
                .description(tenant.getDescription())
                .createdAt(tenant.getCreatedAt())
                .updatedAt(tenant.getUpdatedAt())
                .tenantId(tenant.getTenantId() != null ? tenant.getTenantId() : tenant.getDomain()) // ADICIONADO
                .build();
    }
    

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UsageStats {
        
        private Long totalRequests;
        private Long totalIntegrations;
        private Long totalConnectors;
        private Long monthlyRequests;
        private Long dailyRequests;
        private Double averageResponseTime;
        private Long errorCount;
        private Double errorRate;
        private Long quotaUsed;
        private Long quotaLimit;
        private Double quotaPercentage;
        private LocalDateTime lastActivity;
        private Map<String, Object> additionalMetrics;
        
        
        public boolean isNearQuotaLimit() {
            return quotaPercentage != null && quotaPercentage > 80.0;
        }
        
       
        public boolean isOverQuotaLimit() {
            return quotaUsed != null && quotaLimit != null && quotaUsed > quotaLimit;
        }
        
      
        public static UsageStats createSampleStats() {
            return UsageStats.builder()
                    .totalRequests(15420L)
                    .totalIntegrations(25L)
                    .totalConnectors(8L)
                    .monthlyRequests(3250L)
                    .dailyRequests(105L)
                    .averageResponseTime(245.5)
                    .errorCount(12L)
                    .errorRate(0.08)
                    .quotaUsed(3250L)
                    .quotaLimit(10000L)
                    .quotaPercentage(32.5)
                    .lastActivity(LocalDateTime.now().minusHours(2))
                    .build();
        }
    }
}