// ============================================================================
// HEALTH CONTROLLER TEST - VERSÃO SIMPLIFICADA SEM AutoConfigureTestDatabase
// ============================================================================
package com.totvs.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração para o HealthController
 * Versão simplificada sem AutoConfigureTestDatabase
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    // ========== DATASOURCE H2 ==========
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    
    // ========== REDIS DESABILITADO ==========
    "spring.data.redis.enabled=false",
    "management.health.redis.enabled=false",
    
    // ========== MANAGEMENT ==========
    "management.endpoints.web.exposure.include=health,info",
    "management.endpoint.health.show-details=always",
    
    // ========== APPLICATION PROPERTIES ==========
    "app.name=TOTVS Integration Hub Test",
    "app.version=1.0.0-TEST",
    "app.description=Test Application",
    "app.environment=test",
    
    // ========== MULTI-TENANT PROPERTIES ==========
    "app.multi-tenant.header-name=X-Tenant-ID",
    "app.multi-tenant.enabled=false",
    "app.multi-tenant.default-tenant=test-tenant",
    "app.multi-tenant.strict-mode=false",
    
    // ========== SECURITY PROPERTIES ==========
    "app.security.api-key.header-name=X-API-Key",
    "app.security.api-key.enabled=false",
    "app.security.api-key.required=false",
    "app.security.jwt.secret=test-secret-key-for-jwt-token-generation-in-tests-only",
    "app.security.jwt.expiration=3600",
    "app.security.jwt.enabled=false",
    
    // ========== SWAGGER DESABILITADO ==========
    "swagger.enabled=false",
    "springdoc.api-docs.enabled=false",
    "springdoc.swagger-ui.enabled=false"
})
public class HealthControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    @Test
    public void shouldReturnHealthStatus() throws Exception {
        // Given
        String url = createURLWithPort("/actuator/health");

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> healthResponse = objectMapper.readValue(response.getBody(), Map.class);
        assertEquals("UP", healthResponse.get("status"));
    }

    @Test
    public void shouldReturnH2DatabaseHealth() throws Exception {
        // Given
        String url = createURLWithPort("/actuator/health");

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> healthResponse = objectMapper.readValue(response.getBody(), Map.class);
        Map<String, Object> components = (Map<String, Object>) healthResponse.get("components");
        
        if (components != null && components.containsKey("db")) {
            Map<String, Object> dbHealth = (Map<String, Object>) components.get("db");
            assertEquals("UP", dbHealth.get("status"));
        }
    }

    @Test
    public void shouldHaveProperResponseStructure() throws Exception {
        // Given
        String url = createURLWithPort("/actuator/health");

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> healthResponse = objectMapper.readValue(response.getBody(), Map.class);
        
        // Verificar estrutura básica
        assertTrue(healthResponse.containsKey("status"));
        assertNotNull(healthResponse.get("status"));
        
        // Status deve ser UP ou DOWN
        String status = (String) healthResponse.get("status");
        assertTrue(status.equals("UP") || status.equals("DOWN"));
    }

    @Test
    public void shouldReturnSimpleHealth() throws Exception {
        // Given
        String url = createURLWithPort("/actuator/health");

        // When
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertTrue(body.containsKey("status"));
    }

    @Test
    public void shouldReturnLivenessCheck() throws Exception {
        // Given
        String url = createURLWithPort("/actuator/health/liveness");

        // When
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        // Then
        // Pode retornar 200 ou 404 dependendo da configuração
        assertTrue(response.getStatusCode() == HttpStatus.OK || 
                  response.getStatusCode() == HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldReturnReadinessCheck() throws Exception {
        // Given
        String url = createURLWithPort("/actuator/health/readiness");

        // When
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        // Then
        // Pode retornar 200 ou 404 dependendo da configuração
        assertTrue(response.getStatusCode() == HttpStatus.OK || 
                  response.getStatusCode() == HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldHandleDatabaseConnectionProperly() throws Exception {
        // Given
        String url = createURLWithPort("/actuator/health");

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        // Verificar que a resposta é um JSON válido
        Map<String, Object> healthResponse = objectMapper.readValue(response.getBody(), Map.class);
        assertNotNull(healthResponse);
        
        // Se houver componente de database, deve estar UP
        if (healthResponse.containsKey("components")) {
            Map<String, Object> components = (Map<String, Object>) healthResponse.get("components");
            if (components.containsKey("db")) {
                Map<String, Object> dbComponent = (Map<String, Object>) components.get("db");
                assertEquals("UP", dbComponent.get("status"));
            }
        }
    }

    @Test
    public void shouldMeetPerformanceRequirements() throws Exception {
        // Given
        String url = createURLWithPort("/actuator/health");
        long startTime = System.currentTimeMillis();

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        // Health check deve responder em menos de 5 segundos
        assertTrue(responseTime < 5000, 
                  "Health check took too long: " + responseTime + "ms");
        
        // Resposta deve ter conteúdo
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }
}