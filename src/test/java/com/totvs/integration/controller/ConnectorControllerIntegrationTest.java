package com.totvs.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.totvs.integration.dto.request.TestConnectorRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Connector Controller Integration Tests")
class ConnectorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

    @MockBean
    private RedisTemplate<String, Object> redisTemplate;

    private static final String BASE_URL = "/api/connectors";
    private static final String TENANT_HEADER = "X-Tenant-ID";
    private static final String DEFAULT_TENANT = "test-tenant";

    @BeforeEach
    void setUp() {
        System.out.println("🧪 Setup: Testes de integração com MockMvc");
        System.out.println("🔐 Tenant: " + DEFAULT_TENANT);
    }

    @Test
    @DisplayName("Should return connector types with proper authentication")
    void shouldReturnConnectorTypes() throws Exception {
        System.out.println("📝 Testando: GET /api/connectors/types");
        
        mockMvc.perform(get(BASE_URL + "/types")
                .header(TENANT_HEADER, DEFAULT_TENANT)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
        
        System.out.println("✅ Teste passou!");
    }

    @Test
    @DisplayName("Should handle connector schema request with authentication")
    void shouldReturnConnectorSchema() throws Exception {
        System.out.println("📝 Testando: GET /api/connectors/DATABASE_POSTGRESQL/schema");
        
        mockMvc.perform(get(BASE_URL + "/DATABASE_POSTGRESQL/schema")
                .header(TENANT_HEADER, DEFAULT_TENANT)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
        
        System.out.println("✅ Teste passou!");
    }

    @Test
    @DisplayName("Should handle connector validation with authentication")
    void shouldValidateConnectorConfig() throws Exception {
        System.out.println("📝 Testando: POST /api/connectors/validate");
        
        TestConnectorRequest request = new TestConnectorRequest();
        request.setType("DATABASE_POSTGRESQL");
        
        Map<String, Object> configuration = new HashMap<>();
        configuration.put("host", "localhost");
        configuration.put("port", 5432);
        configuration.put("database", "testdb");
        configuration.put("username", "testuser");
        configuration.put("password", "testpass");
        request.setConfig(configuration);

        mockMvc.perform(post(BASE_URL + "/validate")
                .header(TENANT_HEADER, DEFAULT_TENANT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk());
        
        System.out.println("✅ Teste passou!");
    }

    @Test
    @DisplayName("Should handle connector templates request with authentication")
    void shouldReturnConnectorTemplates() throws Exception {
        System.out.println("📝 Testando: GET /api/connectors/templates");
        
        mockMvc.perform(get(BASE_URL + "/templates")
                .header(TENANT_HEADER, DEFAULT_TENANT)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
        
        System.out.println("✅ Teste passou!");
    }

    @Test
    @DisplayName("Should handle connector test with authentication")
    void shouldTestConnectorConnection() throws Exception {
        System.out.println("📝 Testando: POST /api/connectors/test");
        
        TestConnectorRequest request = new TestConnectorRequest();
        request.setType("DATABASE_POSTGRESQL");
        
        Map<String, Object> configuration = new HashMap<>();
        configuration.put("host", "localhost");
        configuration.put("port", 5432);
        configuration.put("database", "testdb");
        configuration.put("username", "test");
        configuration.put("password", "test");
        request.setConfig(configuration);

        mockMvc.perform(post(BASE_URL + "/test")
                .header(TENANT_HEADER, DEFAULT_TENANT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk());
        
        System.out.println("✅ Teste passou!");
    }

    @Test
    @DisplayName("Should handle invalid connector type with authentication")
    void shouldHandleInvalidConnectorType() throws Exception {
        System.out.println("📝 Testando: GET /api/connectors/INVALID_TYPE/schema");
        
        mockMvc.perform(get(BASE_URL + "/INVALID_TYPE/schema")
                .header(TENANT_HEADER, DEFAULT_TENANT)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().is5xxServerError());
        
        System.out.println("✅ Teste passou!");
    }

    @Test
    @DisplayName("Should respond within reasonable time")
    void shouldMeetPerformanceRequirements() throws Exception {
        System.out.println("📝 Testando Performance: GET /api/connectors/types");
        
        long startTime = System.currentTimeMillis();
        
        mockMvc.perform(get(BASE_URL + "/types")
                .header(TENANT_HEADER, DEFAULT_TENANT)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
        
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("📊 Duration: " + duration + "ms (threshold: 2000ms)");
        
        assert duration < 2000 : "Response took too long: " + duration + "ms";
        System.out.println("✅ Performance test passou!");
    }

    @Test
    @DisplayName("Should handle templates with query parameter and authentication")
    void shouldReturnTemplatesForSpecificType() throws Exception {
        System.out.println("📝 Testando: GET /api/connectors/templates?type=DATABASE_POSTGRESQL");
        
        mockMvc.perform(get(BASE_URL + "/templates?type=DATABASE_POSTGRESQL")
                .header(TENANT_HEADER, DEFAULT_TENANT)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk());
        
        System.out.println("✅ Teste passou!");
    }
}