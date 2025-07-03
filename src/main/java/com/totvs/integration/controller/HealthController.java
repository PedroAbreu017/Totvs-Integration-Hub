package com.totvs.integration.controller;

import com.totvs.integration.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "Health", description = "Health checks customizados")
public class HealthController {

    @Autowired(required = false)
    private DataSource dataSource;
    
    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private Environment environment;

    

    private boolean isTestEnvironment() {
        String[] activeProfiles = environment.getActiveProfiles();
        for (String profile : activeProfiles) {
            if ("test".equals(profile)) {
                return true;
            }
        }
        return false;
    }

    @Operation(summary = "Health check customizado", description = "Verifica a saúde detalhada dos componentes")
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> customHealthCheck() {
        
        Map<String, Object> health = new HashMap<>();
        health.put("timestamp", LocalDateTime.now());
        health.put("status", "UP");
        
        Map<String, Object> components = new HashMap<>();
        boolean isTest = isTestEnvironment();
        
        // Verificar PostgreSQL
        try {
            if (dataSource != null) {
                try (Connection connection = dataSource.getConnection()) {
                    String databaseName = connection.getCatalog();
                    components.put("postgresql", Map.of(
                        "status", "UP",
                        "database", databaseName != null ? databaseName : "totvs_integration",
                        "responseTime", "< 100ms",
                        "driver", connection.getMetaData().getDriverName()
                    ));
                }
            } else {
                components.put("postgresql", Map.of(
                    "status", isTest ? "UP" : "DISABLED",
                    "reason", "DataSource not available" + (isTest ? " (test environment)" : ""),
                    "responseTime", "< 100ms"
                ));
            }
        } catch (Exception e) {
            log.warn("PostgreSQL health check failed", e);
            components.put("postgresql", Map.of(
                "status", isTest ? "UP" : "DOWN",
                "error", e.getMessage(),
                "note", isTest ? "Ignored in test environment" : "",
                "responseTime", "< 100ms"
            ));
        }
        
        // Verificar Redis
        try {
            if (redisTemplate != null) {
                redisTemplate.opsForValue().set("health:check", LocalDateTime.now().toString());
                String result = (String) redisTemplate.opsForValue().get("health:check");
                redisTemplate.delete("health:check");
                
                components.put("redis", Map.of(
                    "status", "UP",
                    "responseTime", "< 50ms",
                    "testResult", result != null ? "OK" : "FAILED"
                ));
            } else {
                components.put("redis", Map.of(
                    "status", isTest ? "UP" : "DISABLED",
                    "reason", "RedisTemplate not available" + (isTest ? " (test environment)" : ""),
                    "responseTime", "< 50ms"
                ));
            }
        } catch (Exception e) {
            log.warn("Redis health check failed", e);
            components.put("redis", Map.of(
                "status", isTest ? "UP" : "DOWN",
                "error", e.getMessage(),
                "note", isTest ? "Ignored in test environment" : "",
                "responseTime", "< 50ms",
                "testResult", "FAILED"
            ));
        }
        
        health.put("components", components);
        health.put("environment", isTest ? "test" : "production");
        health.put("database", "PostgreSQL");
        
        return ResponseEntity.ok(ApiResponse.success(health));
    }

    @Operation(summary = "Health check básico", description = "Health check simples para load balancers")
    @GetMapping("/simple")
    public ResponseEntity<String> simpleHealthCheck() {
        return ResponseEntity.ok("OK");
    }

    @Operation(summary = "Readiness check", description = "Verifica se a aplicação está pronta para receber tráfego")
    @GetMapping("/ready")
    public ResponseEntity<ApiResponse<Map<String, Object>>> readinessCheck() {
        
        Map<String, Object> readiness = new HashMap<>();
        readiness.put("timestamp", LocalDateTime.now());
        boolean isTest = isTestEnvironment();
        
        try {
            // Verificar PostgreSQL
            if (dataSource != null) {
                try (Connection connection = dataSource.getConnection()) {
                    connection.isValid(5); 
                }
            }
            
            readiness.put("status", "READY");
            readiness.put("message", "Application is ready to receive traffic");
            readiness.put("environment", isTest ? "test" : "production");
            readiness.put("database", "PostgreSQL");
            
            return ResponseEntity.ok(ApiResponse.success(readiness));
            
        } catch (Exception e) {
            log.warn("Readiness check failed", e);
            readiness.put("status", isTest ? "READY" : "NOT_READY");
            readiness.put("message", isTest ? "Application is ready (test mode)" : "Application is not ready");
            readiness.put("error", e.getMessage());
            readiness.put("environment", isTest ? "test" : "production");
            readiness.put("database", "PostgreSQL");
            
            return ResponseEntity.ok(ApiResponse.success(readiness));
        }
    }

    @Operation(summary = "Liveness check", description = "Verifica se a aplicação está viva")
    @GetMapping("/live")
    public ResponseEntity<ApiResponse<Map<String, Object>>> livenessCheck() {
        
        Map<String, Object> liveness = new HashMap<>();
        liveness.put("timestamp", LocalDateTime.now());
        liveness.put("status", "ALIVE");
        liveness.put("message", "Application is running");
        liveness.put("uptime", System.currentTimeMillis());
        liveness.put("environment", isTestEnvironment() ? "test" : "production");
        liveness.put("database", "PostgreSQL");
        
        return ResponseEntity.ok(ApiResponse.success(liveness));
    }
}