package com.totvs.integration.security;

import com.totvs.integration.entity.Tenant;  
import com.totvs.integration.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TenantResolver {
    
    private final TenantRepository tenantRepository;
    
    @Value("${app.multi-tenant.default-tenant:default}")
    private String defaultTenantId;
    
    @Cacheable(value = "tenants", key = "#tenantId")
    public Optional<TenantContext> resolveTenantById(String tenantId) {
        if (tenantId == null || tenantId.trim().isEmpty()) {
            tenantId = defaultTenantId;
        }
        
        
        Optional<Tenant> tenant = tenantRepository.findByTenantId(tenantId);
        if (tenant.isEmpty()) {
            tenant = tenantRepository.findByDomain(tenantId);
        }
        
        return tenant
                .filter(t -> t.getStatus() == Tenant.Status.ACTIVE) 
                .map(this::toTenantContext);
    }
    
    @Cacheable(value = "tenants", key = "#apiKey")
    public Optional<TenantContext> resolveTenantByApiKey(String apiKey) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return Optional.empty();
        }
        
        return tenantRepository.findByApiKey(apiKey)
                .filter(t -> t.getStatus() == Tenant.Status.ACTIVE) 
                .map(this::toTenantContext);
    }
    
    public boolean isValidTenant(String tenantId) {
        return resolveTenantById(tenantId).isPresent();
    }
    
    public boolean isValidApiKey(String apiKey) {
        return resolveTenantByApiKey(apiKey).isPresent();
    }
    
    private TenantContext toTenantContext(Tenant tenant) {
        return TenantContext.builder()
                .tenantId(tenant.getTenantId() != null ? tenant.getTenantId() : tenant.getDomain()) 
                .apiKey(tenant.getApiKey())
                .tenantName(tenant.getName())
                .maxRequestsPerMinute(tenant.getMaxRequestsPerMinute())
                .maxConcurrentIntegrations(tenant.getMaxConcurrentIntegrations())
                .build();
    }
}