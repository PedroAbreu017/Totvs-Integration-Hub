package com.totvs.integration.service;

import com.totvs.integration.entity.Tenant;
import com.totvs.integration.dto.request.CreateTenantRequest;
import com.totvs.integration.dto.response.TenantResponse;
import com.totvs.integration.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;

   
    public TenantResponse createTenant(CreateTenantRequest request) {
        log.info("Criando novo tenant: {}", request.getName());
        
        if (tenantRepository.existsByDomain(request.getDomain())) {
            throw new IllegalArgumentException("Domínio já existe: " + request.getDomain());
        }
        
      
        Tenant tenant = new Tenant();
        tenant.setId(UUID.randomUUID().toString());
        tenant.setName(request.getName());
        tenant.setDomain(request.getDomain());
        tenant.setEmail(request.getEmail());
        tenant.setPlan(request.getPlan());
        tenant.setStatus(Tenant.Status.ACTIVE);
        tenant.setApiKey(generateApiKey());
        tenant.setSettings(request.getSettings());
        tenant.setDescription(request.getDescription());
        tenant.setCreatedAt(LocalDateTime.now());
        tenant.setUpdatedAt(LocalDateTime.now());
        
        
        Tenant savedTenant = tenantRepository.save(tenant);
        log.info("Tenant criado com sucesso: {} (ID: {})", savedTenant.getName(), savedTenant.getId());
        
        return TenantResponse.fromTenant(savedTenant);
    }

   
    public Page<TenantResponse> listTenants(Pageable pageable, String status, String plan) {
        log.info("Listando tenants com filtros - status: {}, plan: {}", status, plan);
        
        Page<Tenant> tenants;
        
        if (status != null && plan != null) {
            Tenant.Status tenantStatus = Tenant.Status.valueOf(status.toUpperCase());
            Tenant.Plan tenantPlan = Tenant.Plan.valueOf(plan.toUpperCase());
            tenants = tenantRepository.findByPlanAndStatus(tenantPlan, tenantStatus, pageable);
        } else if (status != null) {
            Tenant.Status tenantStatus = Tenant.Status.valueOf(status.toUpperCase());
            tenants = tenantRepository.findByStatus(tenantStatus, pageable);
        } else if (plan != null) {
            Tenant.Plan tenantPlan = Tenant.Plan.valueOf(plan.toUpperCase());
            tenants = tenantRepository.findByPlan(tenantPlan, pageable);
        } else {
            tenants = tenantRepository.findAll(pageable);
        }
        
        List<TenantResponse> responses = tenants.getContent()
                .stream()
                .map(TenantResponse::fromTenant)
                .toList();
        
        return new PageImpl<>(responses, pageable, tenants.getTotalElements());
    }

   
    public TenantResponse getTenant(String tenantId) {
        log.info("Buscando tenant por ID: {}", tenantId);
        
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant não encontrado: " + tenantId));
        
        return TenantResponse.fromTenant(tenant);
    }

   
    public Optional<TenantResponse> getTenantById(String tenantId) {
        log.info("Buscando tenant por ID: {}", tenantId);
        
        return tenantRepository.findById(tenantId)
                .map(TenantResponse::fromTenant);
    }

    
    public List<TenantResponse> getAllTenants() {
        log.info("Listando todos os tenants");
        
        return tenantRepository.findAll()
                .stream()
                .map(TenantResponse::fromTenant)
                .toList();
    }

   
    public TenantResponse updateTenant(String tenantId, CreateTenantRequest request) {
        log.info("Atualizando tenant: {}", tenantId);
        
        Tenant existingTenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant não encontrado: " + tenantId));
        
        
        if (!existingTenant.getDomain().equals(request.getDomain()) && 
            tenantRepository.existsByDomain(request.getDomain())) {
            throw new IllegalArgumentException("Domínio já existe: " + request.getDomain());
        }
        
        
        existingTenant.setName(request.getName());
        existingTenant.setDomain(request.getDomain());
        existingTenant.setEmail(request.getEmail());
        existingTenant.setPlan(request.getPlan());
        existingTenant.setSettings(request.getSettings());
        existingTenant.setDescription(request.getDescription());
        existingTenant.setUpdatedAt(LocalDateTime.now());
        
        Tenant updatedTenant = tenantRepository.save(existingTenant);
        log.info("Tenant atualizado com sucesso: {}", tenantId);
        
        return TenantResponse.fromTenant(updatedTenant);
    }

    
    public void deleteTenant(String tenantId) {
        log.info("Deletando tenant: {}", tenantId);
        
        if (!tenantRepository.existsById(tenantId)) {
            throw new IllegalArgumentException("Tenant não encontrado: " + tenantId);
        }
        
        tenantRepository.deleteById(tenantId);
        log.info("Tenant deletado com sucesso: {}", tenantId);
    }


    public TenantResponse regenerateApiKey(String tenantId) {
        log.info("Regenerando API key para tenant: {}", tenantId);
        
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("Tenant não encontrado: " + tenantId));
        
        tenant.setApiKey(generateApiKey());
        tenant.setUpdatedAt(LocalDateTime.now());
        
        Tenant updatedTenant = tenantRepository.save(tenant);
        log.info("API key regenerada para tenant: {}", tenantId);
        
        return TenantResponse.fromTenant(updatedTenant);
    }

   
    public TenantResponse.UsageStats getTenantStats(String tenantId) {
        log.info("Obtendo estatísticas para tenant: {}", tenantId);
        
        
        if (!tenantRepository.existsById(tenantId)) {
            throw new IllegalArgumentException("Tenant não encontrado: " + tenantId);
        }
        
        
        return TenantResponse.UsageStats.createSampleStats();
    }

   
    private String generateApiKey() {
        String apiKey;
        do {
            apiKey = "tk_" + UUID.randomUUID().toString().replace("-", "");
        } while (tenantRepository.existsByApiKey(apiKey));
        
        return apiKey;
    }
}