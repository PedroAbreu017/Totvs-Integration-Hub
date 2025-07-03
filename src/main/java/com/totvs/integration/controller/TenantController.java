
package com.totvs.integration.controller;

import com.totvs.integration.dto.request.CreateTenantRequest;
import com.totvs.integration.dto.response.ApiResponse;
import com.totvs.integration.dto.response.TenantResponse;
import com.totvs.integration.service.TenantService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/tenants")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @PostMapping
    public ResponseEntity<ApiResponse<TenantResponse>> createTenant(
            @Valid @RequestBody CreateTenantRequest request) {
        
        log.info("Creating tenant: {}", request.getTenantId());
        
        TenantResponse tenant = tenantService.createTenant(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(tenant, "Tenant criado com sucesso"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TenantResponse>>> listTenants(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String plan) {
        
        log.debug("Listing tenants with filters - status: {}, plan: {}", status, plan);
        
        Page<TenantResponse> tenants = tenantService.listTenants(pageable, status, plan);
        
        return ResponseEntity.ok(ApiResponse.success(tenants));
    }

    @GetMapping("/{tenantId}")
    public ResponseEntity<ApiResponse<TenantResponse>> getTenant(@PathVariable String tenantId) {
        
        log.debug("Getting tenant: {}", tenantId);
        
        TenantResponse tenant = tenantService.getTenant(tenantId);
        
        return ResponseEntity.ok(ApiResponse.success(tenant));
    }

    @PutMapping("/{tenantId}")
    public ResponseEntity<ApiResponse<TenantResponse>> updateTenant(
            @PathVariable String tenantId,
            @Valid @RequestBody CreateTenantRequest request) {
        
        log.info("Updating tenant: {}", tenantId);
        
        TenantResponse tenant = tenantService.updateTenant(tenantId, request);
        
        return ResponseEntity.ok(ApiResponse.success(tenant, "Tenant atualizado"));
    }

    @DeleteMapping("/{tenantId}")
    public ResponseEntity<ApiResponse<Void>> deleteTenant(@PathVariable String tenantId) {
        
        log.info("Deleting tenant: {}", tenantId);
        
        tenantService.deleteTenant(tenantId);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Tenant removido"));
    }

    @PostMapping("/{tenantId}/regenerate-api-key")
    public ResponseEntity<ApiResponse<TenantResponse>> regenerateApiKey(@PathVariable String tenantId) {
        
        log.info("Regenerating API key for tenant: {}", tenantId);
        
        TenantResponse tenant = tenantService.regenerateApiKey(tenantId);
        
        return ResponseEntity.ok(ApiResponse.success(tenant, "API Key regenerada"));
    }

    @GetMapping("/{tenantId}/stats")
    public ResponseEntity<ApiResponse<TenantResponse.UsageStats>> getTenantStats(@PathVariable String tenantId) {
        
        log.debug("Getting stats for tenant: {}", tenantId);
        
        TenantResponse.UsageStats stats = tenantService.getTenantStats(tenantId);
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
