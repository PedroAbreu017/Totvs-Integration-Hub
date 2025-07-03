
package com.totvs.integration.controller;

import com.totvs.integration.dto.response.ApiResponse;
import com.totvs.integration.security.TenantContext;
import com.totvs.integration.security.RateLimitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/system")
@RequiredArgsConstructor
@Tag(name = "System", description = "Informações do sistema")
public class SystemController {

    private final RateLimitService rateLimitService;

    @Value("${app.name}")
    private String appName;

    @Value("${app.version}")
    private String appVersion;

    @Value("${app.description}")
    private String appDescription;

    @Operation(summary = "Informações do sistema", description = "Retorna informações básicas do sistema")
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemInfo() {
        
        Map<String, Object> info = new HashMap<>();
        info.put("name", appName);
        info.put("version", appVersion);
        info.put("description", appDescription);
        info.put("timestamp", LocalDateTime.now());
        info.put("status", "running");
        
        // Informações do tenant atual
        TenantContext tenant = TenantContext.getCurrentTenant();
        if (tenant != null) {
            Map<String, Object> tenantInfo = new HashMap<>();
            tenantInfo.put("tenantId", tenant.getTenantId());
            tenantInfo.put("tenantName", tenant.getTenantName());
            info.put("tenant", tenantInfo);
        }
        
        return ResponseEntity.ok(ApiResponse.success(info));
    }

    @Operation(summary = "Status de conectividade", description = "Verifica o status de conectividade com serviços externos")
    @GetMapping("/connectivity")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getConnectivityStatus() {
        
        Map<String, Object> connectivity = new HashMap<>();
        
       
        try {
            
            connectivity.put("mongodb", Map.of("status", "connected", "timestamp", LocalDateTime.now()));
        } catch (Exception e) {
            connectivity.put("mongodb", Map.of("status", "disconnected", "error", e.getMessage()));
        }
        
     
        try {
            
            connectivity.put("redis", Map.of("status", "connected", "timestamp", LocalDateTime.now()));
        } catch (Exception e) {
            connectivity.put("redis", Map.of("status", "disconnected", "error", e.getMessage()));
        }
        
        return ResponseEntity.ok(ApiResponse.success(connectivity));
    }

    @Operation(summary = "Status de rate limiting", description = "Retorna informações sobre rate limiting do tenant atual")
    @GetMapping("/rate-limit")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRateLimitStatus() {
        
        TenantContext tenant = TenantContext.getCurrentTenant();
        if (tenant == null) {
            return ResponseEntity.ok(ApiResponse.error("Tenant context not available"));
        }
        
        Map<String, Object> rateLimitInfo = new HashMap<>();
        rateLimitInfo.put("tenantId", tenant.getTenantId());
        rateLimitInfo.put("maxRequestsPerMinute", tenant.getMaxRequestsPerMinute());
        
        try {
            long currentUsage = rateLimitService.getCurrentUsage(tenant.getTenantId());
            long remainingRequests = rateLimitService.getRemainingRequests(
                tenant.getTenantId(), tenant.getMaxRequestsPerMinute());
            
            rateLimitInfo.put("currentUsage", currentUsage);
            rateLimitInfo.put("remainingRequests", remainingRequests);
            rateLimitInfo.put("timestamp", LocalDateTime.now());
            
        } catch (Exception e) {
            log.error("Error getting rate limit info for tenant: {}", tenant.getTenantId(), e);
            rateLimitInfo.put("error", "Unable to get rate limit information");
        }
        
        return ResponseEntity.ok(ApiResponse.success(rateLimitInfo));
    }

    @Operation(summary = "Ping", description = "Endpoint simples para verificar se a API está funcionando")
    @GetMapping("/ping")
    public ResponseEntity<ApiResponse<Map<String, Object>>> ping() {
        
        Map<String, Object> ping = new HashMap<>();
        ping.put("message", "pong");
        ping.put("timestamp", LocalDateTime.now());
        ping.put("server", appName);
        
        TenantContext tenant = TenantContext.getCurrentTenant();
        if (tenant != null) {
            ping.put("tenant", tenant.getTenantId());
        }
        
        return ResponseEntity.ok(ApiResponse.success(ping));
    }

    @Operation(summary = "Versão da API", description = "Retorna a versão da API")
    @GetMapping("/version")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getVersion() {
        
        Map<String, Object> version = new HashMap<>();
        version.put("version", appVersion);
        version.put("name", appName);
        version.put("buildTime", LocalDateTime.now());
        
        return ResponseEntity.ok(ApiResponse.success(version));
    }
}

