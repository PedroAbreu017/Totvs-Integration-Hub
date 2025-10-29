package com.totvs.integration.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class HealthControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    // Mock RedisConnectionFactory e RedisTemplate
    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

    @MockBean
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void shouldReturnHealthStatus() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/health", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldReturnSimpleHealth() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/health/simple", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldReturnLivenessCheck() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/health/live", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldReturnReadinessCheck() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/health/ready", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldReturnH2DatabaseHealth() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/health", String.class);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("status"));
    }

    @Test
    void shouldHaveProperResponseStructure() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/health", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldHandleDatabaseConnectionProperly() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/health", String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldMeetPerformanceRequirements() {
        long start = System.currentTimeMillis();
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/health", String.class);
        long duration = System.currentTimeMillis() - start;
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(duration < 5000, "Health check should respond in less than 5 seconds");
    }
}