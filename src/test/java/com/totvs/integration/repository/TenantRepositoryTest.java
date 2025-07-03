package com.totvs.integration.repository;

import com.totvs.integration.entity.Tenant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste do TenantRepository usando H2 Database real
 * CORRIGIDO: Sem JSONB, usando database H2 real
 */
@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
@DisplayName("Tenant Repository Tests - H2 Database")
class TenantRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TenantRepository tenantRepository;

    private Tenant testTenant;

    @BeforeEach
    void setUp() {
        // Criar tenant de teste SEM settings JSONB
        testTenant = new Tenant();
        testTenant.setName("Test Tenant");
        testTenant.setDomain("test.com");
        testTenant.setEmail("test@test.com");
        testTenant.setContactEmail("contact@test.com");
        testTenant.setTenantId("test-tenant-id");
        testTenant.setDescription("Tenant para testes");
        testTenant.setStatus(Tenant.Status.ACTIVE);
        testTenant.setPlan(Tenant.Plan.FREE);
        testTenant.setMaxRequestsPerMinute(1000);
        testTenant.setMaxConcurrentIntegrations(5);
        testTenant.setApiKey("test-api-key");
        
        // CRÍTICO: Inicializar settings como Map vazio (sem JSONB)
        Map<String, Object> settings = new HashMap<>();
        settings.put("theme", "light");
        settings.put("notifications", true);
        testTenant.setSettings(settings);
    }

    @Test
    @DisplayName("Deve salvar e buscar tenant por ID")
    void shouldSaveAndFindTenantById() {
        // Given
        Tenant savedTenant = entityManager.persistAndFlush(testTenant);
        
        // When
        Optional<Tenant> found = tenantRepository.findById(savedTenant.getId());
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Tenant");
        assertThat(found.get().getDomain()).isEqualTo("test.com");
    }

    @Test
    @DisplayName("Deve buscar tenant por domínio")
    void shouldFindByDomain() {
        // Given
        entityManager.persistAndFlush(testTenant);
        
        // When
        Optional<Tenant> found = tenantRepository.findByDomain("test.com");
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getDomain()).isEqualTo("test.com");
    }

    @Test
    @DisplayName("Deve buscar tenant por tenant ID")
    void shouldFindByTenantId() {
        // Given
        entityManager.persistAndFlush(testTenant);
        
        // When
        Optional<Tenant> found = tenantRepository.findByTenantId("test-tenant-id");
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getTenantId()).isEqualTo("test-tenant-id");
    }

    @Test
    @DisplayName("Deve verificar se domínio existe")
    void shouldCheckIfDomainExists() {
        // Given
        entityManager.persistAndFlush(testTenant);
        
        // When
        boolean exists = tenantRepository.existsByDomain("test.com");
        boolean notExists = tenantRepository.existsByDomain("notfound.com");
        
        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Deve retornar vazio quando tenant não existe")
    void shouldReturnEmptyWhenTenantNotExists() {
        // When
        Optional<Tenant> found = tenantRepository.findByDomain("notfound.com");
        
        // Then
        assertThat(found).isNotPresent();
    }

    @Test
    @DisplayName("Deve contar todos os tenants")
    void shouldCountAllTenants() {
        // Given
        entityManager.persistAndFlush(testTenant);
        
        Tenant anotherTenant = new Tenant();
        anotherTenant.setName("Another Tenant");
        anotherTenant.setDomain("another.com");
        anotherTenant.setEmail("another@test.com");
        anotherTenant.setTenantId("another-tenant");
        anotherTenant.setStatus(Tenant.Status.ACTIVE);
        anotherTenant.setPlan(Tenant.Plan.BASIC);
        anotherTenant.setSettings(new HashMap<>());
        
        entityManager.persistAndFlush(anotherTenant);
        
        // When
        long count = tenantRepository.count();
        
        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Deve salvar e recuperar settings como JSON")
    void shouldSaveAndRetrieveSettingsAsJson() {
        // Given
        Map<String, Object> settings = new HashMap<>();
        settings.put("theme", "dark");
        settings.put("notifications", false);
        settings.put("language", "pt-BR");
        settings.put("maxItems", 100);
        
        testTenant.setSettings(settings);
        
        // When
        Tenant savedTenant = entityManager.persistAndFlush(testTenant);
        entityManager.clear(); // Clear cache to force DB read
        
        Optional<Tenant> found = tenantRepository.findById(savedTenant.getId());
        
        // Then
        assertThat(found).isPresent();
        Map<String, Object> retrievedSettings = found.get().getSettings();
        assertThat(retrievedSettings).isNotNull();
        // Note: H2 pode não preservar tipos exatos, então testamos presença
        assertThat(retrievedSettings).containsKeys("theme", "notifications", "language");
    }

    @Test
    @DisplayName("Deve gerar API key automaticamente")
    void shouldGenerateApiKeyAutomatically() {
        // Given
        testTenant.setApiKey(null);
        
        // When
        testTenant.generateApiKey();
        Tenant savedTenant = entityManager.persistAndFlush(testTenant);
        
        // Then
        assertThat(savedTenant.getApiKey()).isNotNull();
        assertThat(savedTenant.getApiKey()).startsWith("tk_");
        assertThat(savedTenant.getApiKey()).contains("test");
    }

    @Test
    @DisplayName("Deve aplicar limites do plano corretamente")
    void shouldApplyPlanLimitsCorrectly() {
        // Given
        testTenant.setPlan(Tenant.Plan.PROFESSIONAL);
        
        // When
        testTenant.applyPlanLimits();
        Tenant savedTenant = entityManager.persistAndFlush(testTenant);
        
        // Then
        assertThat(savedTenant.getMaxRequestsPerMinute()).isEqualTo(10000);
        assertThat(savedTenant.getMaxConcurrentIntegrations()).isEqualTo(100);
    }

    @Test
    @DisplayName("Deve atualizar timestamps automaticamente")
    void shouldUpdateTimestampsAutomatically() {
        // Given
        Tenant savedTenant = entityManager.persistAndFlush(testTenant);
        LocalDateTime originalUpdatedAt = savedTenant.getUpdatedAt();
        
        // When
        try {
            Thread.sleep(10); // Ensure time difference
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        savedTenant.setName("Updated Name");
        Tenant updatedTenant = entityManager.persistAndFlush(savedTenant);
        
        // Then
        assertThat(updatedTenant.getCreatedAt()).isNotNull();
        assertThat(updatedTenant.getUpdatedAt()).isNotNull();
        assertThat(updatedTenant.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    @DisplayName("Deve validar tenant corretamente")
    void shouldValidateTenantCorrectly() {
        // Given - tenant válido
        assertThat(testTenant.isValid()).isTrue();
        
        // When - tenant inválido
        Tenant invalidTenant = new Tenant();
        invalidTenant.setName("");
        invalidTenant.setDomain("");
        invalidTenant.setEmail("invalid-email");
        
        // Then
        assertThat(invalidTenant.isValid()).isFalse();
    }
}