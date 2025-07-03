// ============================================================================
// TenantServiceTest.java - CORRIGIDO PARA SEUS DTOs
// ============================================================================
package com.totvs.integration.service;

import com.totvs.integration.entity.Tenant;
import com.totvs.integration.repository.TenantRepository;
import com.totvs.integration.dto.request.CreateTenantRequest;
import com.totvs.integration.dto.response.TenantResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TenantServiceTest {

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private TenantService tenantService;

    private Tenant testTenant;
    private CreateTenantRequest createRequest;

    @BeforeEach
    void setUp() {
        testTenant = new Tenant();
        testTenant.setId("507f1f77bcf86cd799439011");
        testTenant.setTenantId("test-tenant");
        testTenant.setName("Test Tenant");
        testTenant.setDescription("Tenant para testes");
        testTenant.setDomain("test.com");
        testTenant.setEmail("test@example.com");
        testTenant.setContactEmail("test@example.com");
        testTenant.setStatus(Tenant.Status.ACTIVE);
        testTenant.setPlan(Tenant.Plan.FREE);
        testTenant.setCreatedAt(LocalDateTime.now());
        testTenant.setUpdatedAt(LocalDateTime.now());

        createRequest = new CreateTenantRequest();
        createRequest.setName("Test Tenant");
        createRequest.setDescription("Tenant para testes");
        createRequest.setDomain("test.com");
        createRequest.setEmail("test@example.com");
        createRequest.setPlan(Tenant.Plan.FREE);
        createRequest.setTenantId("test-tenant");  // ✅ USANDO SEU DTO
        createRequest.setSettings(new HashMap<>());
    }

    @Test
    @DisplayName("Deve criar novo tenant")
    void shouldCreateNewTenant() {
        // Given
        when(tenantRepository.existsByDomain(anyString())).thenReturn(false);
        when(tenantRepository.save(any(Tenant.class))).thenReturn(testTenant);
        
        // When
        TenantResponse createdTenant = tenantService.createTenant(createRequest);
        
        // Then
        assertThat(createdTenant).isNotNull();
        assertThat(createdTenant.getName()).isEqualTo("Test Tenant");
        assertThat(createdTenant.getTenantId()).isEqualTo("test-tenant");  // ✅ USANDO SEU MÉTODO
        verify(tenantRepository).existsByDomain("test.com");
        verify(tenantRepository).save(any(Tenant.class));
    }

    @Test
    @DisplayName("Deve buscar tenant por ID")
    void shouldFindTenantById() {
        // Given
        when(tenantRepository.findById("test-tenant"))
            .thenReturn(Optional.of(testTenant));
        
        // When
        TenantResponse foundTenant = tenantService.getTenant("test-tenant");
        
        // Then
        assertThat(foundTenant).isNotNull();
        assertThat(foundTenant.getName()).isEqualTo("Test Tenant");
        assertThat(foundTenant.getTenantId()).isEqualTo("test-tenant");  // ✅ USANDO SEU MÉTODO
        verify(tenantRepository).findById("test-tenant");
    }

    @Test
    @DisplayName("Deve listar todos os tenants")
    void shouldListAllTenants() {
        // Given
        Tenant secondTenant = new Tenant();
        secondTenant.setTenantId("tenant-2");
        secondTenant.setName("Second Tenant");
        secondTenant.setDomain("second.com");
        secondTenant.setEmail("second@example.com");
        secondTenant.setStatus(Tenant.Status.ACTIVE);
        secondTenant.setPlan(Tenant.Plan.BASIC);
        
        when(tenantRepository.findAll())
            .thenReturn(Arrays.asList(testTenant, secondTenant));
        
        // When
        List<TenantResponse> tenants = tenantService.getAllTenants();
        
        // Then
        assertThat(tenants).hasSize(2);
        assertThat(tenants).extracting(TenantResponse::getName)
            .contains("Test Tenant", "Second Tenant");
        verify(tenantRepository).findAll();
    }

    @Test
    @DisplayName("Deve atualizar tenant existente")
    void shouldUpdateExistingTenant() {
        // Given - Tenant atualizado com DOMAIN não nulo
        Tenant updatedTenant = new Tenant();
        updatedTenant.setId("507f1f77bcf86cd799439011");
        updatedTenant.setTenantId("test-tenant");
        updatedTenant.setName("Updated Tenant");
        updatedTenant.setDescription("Updated description");
        updatedTenant.setDomain("updated.com");  // ✅ DOMAIN NÃO NULO
        updatedTenant.setEmail("updated@example.com");
        updatedTenant.setContactEmail("updated@example.com");
        updatedTenant.setStatus(Tenant.Status.ACTIVE);
        updatedTenant.setPlan(Tenant.Plan.BASIC);
        updatedTenant.setCreatedAt(testTenant.getCreatedAt());
        updatedTenant.setUpdatedAt(LocalDateTime.now());

        when(tenantRepository.findById("test-tenant"))
            .thenReturn(Optional.of(testTenant));
        when(tenantRepository.existsByDomain("updated.com"))
            .thenReturn(false);
        when(tenantRepository.save(any(Tenant.class)))
            .thenReturn(updatedTenant);
        
        // When
        createRequest.setName("Updated Tenant");
        createRequest.setDomain("updated.com");  // ✅ DOMAIN ATUALIZADO
        createRequest.setEmail("updated@example.com");
        TenantResponse updated = tenantService.updateTenant("test-tenant", createRequest);
        
        // Then
        assertThat(updated.getName()).isEqualTo("Updated Tenant");
        verify(tenantRepository).findById("test-tenant");
        verify(tenantRepository).existsByDomain("updated.com");
        verify(tenantRepository).save(any(Tenant.class));
    }

    @Test
    @DisplayName("Deve deletar tenant")
    void shouldDeleteTenant() {
        // Given
        when(tenantRepository.existsById("test-tenant")).thenReturn(true);
        
        // When
        tenantService.deleteTenant("test-tenant");
        
        // Then
        verify(tenantRepository).existsById("test-tenant");
        verify(tenantRepository).deleteById("test-tenant");
    }

    @Test
    @DisplayName("Deve verificar se tenant está ativo")
    void shouldCheckIfTenantIsActive() {
        // Given
        testTenant.setStatus(Tenant.Status.ACTIVE);
        
        // When
        boolean isActive = testTenant.isActive();
        
        // Then
        assertThat(isActive).isTrue();
    }

    @Test
    @DisplayName("Deve aplicar limites do plano")
    void shouldApplyPlanLimits() {
        // Given
        testTenant.setPlan(Tenant.Plan.BASIC);
        
        // When
        testTenant.applyPlanLimits();
        
        // Then
        assertThat(testTenant.getMaxRequestsPerMinute())
            .isEqualTo(Tenant.Plan.BASIC.getMaxRequestsPerMinute());
        assertThat(testTenant.getMaxConcurrentIntegrations())
            .isEqualTo(Tenant.Plan.BASIC.getMaxConcurrentIntegrations());
    }
}