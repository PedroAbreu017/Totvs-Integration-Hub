package com.totvs.integration.security;

import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantContext {
    
    private String tenantId;
    private String apiKey;
    private String tenantName;
    private Integer maxRequestsPerMinute;
    private Integer maxConcurrentIntegrations;
    
  
    private static final ThreadLocal<TenantContext> CURRENT_TENANT = new ThreadLocal<>();
    
    
    public boolean hasUnlimitedAccess() {
        return maxRequestsPerMinute == -1 || maxConcurrentIntegrations == -1;
    }
    
    
    public boolean isWithinRequestLimit(int currentRequests) {
        return hasUnlimitedAccess() || currentRequests <= maxRequestsPerMinute;
    }
    
    
    public boolean isWithinIntegrationLimit(int currentIntegrations) {
        return hasUnlimitedAccess() || currentIntegrations <= maxConcurrentIntegrations;
    }
    
   
    public static void setCurrentTenant(TenantContext tenantContext) {
        CURRENT_TENANT.set(tenantContext);
    }
    
    
    public static TenantContext getCurrentTenant() {
        return CURRENT_TENANT.get();
    }
    

    public static String getCurrentTenantId() {
        TenantContext current = getCurrentTenant();
        return current != null ? current.getTenantId() : null;
    }
    
  
    public static void clear() {
        CURRENT_TENANT.remove();
    }
    
    
    public static boolean hasCurrentTenant() {
        return getCurrentTenant() != null;
    }
}