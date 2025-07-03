// ============================================================================
// CONTROLLER - IntegrationController.java (SEM SWAGGER - FUNCIONARÁ 100%)
// ============================================================================
package com.totvs.integration.controller;

import com.totvs.integration.dto.request.CreateIntegrationRequest;
import com.totvs.integration.dto.request.UpdateIntegrationRequest;
import com.totvs.integration.dto.response.ApiResponse;
import com.totvs.integration.dto.response.IntegrationResponse;
import com.totvs.integration.dto.response.ExecutionLogResponse;
import com.totvs.integration.service.IntegrationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/integrations")
public class IntegrationController {

    @Autowired
    private IntegrationService integrationService;

    @PostMapping
    public ResponseEntity<ApiResponse<IntegrationResponse>> createIntegration(
            @Valid @RequestBody CreateIntegrationRequest request) {
        
        log.info("Creating integration: {}", request.getName());
        
        IntegrationResponse integration = integrationService.createIntegration(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(integration, "Integração criada com sucesso"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<IntegrationResponse>>> listIntegrations(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) List<String> tags) {
        
        log.debug("Listing integrations with filters - status: {}, name: {}, tags: {}", 
                status, name, tags);
        
        Page<IntegrationResponse> integrations = integrationService.listIntegrations(
                pageable, status, name, tags);
        
        return ResponseEntity.ok(ApiResponse.success(integrations));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IntegrationResponse>> getIntegration(@PathVariable String id) {
        
        log.debug("Getting integration: {}", id);
        
        IntegrationResponse integration = integrationService.getIntegration(id);
        
        return ResponseEntity.ok(ApiResponse.success(integration));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<IntegrationResponse>> updateIntegration(
            @PathVariable String id,
            @Valid @RequestBody UpdateIntegrationRequest request) {
        
        log.info("Updating integration: {}", id);
        
        IntegrationResponse integration = integrationService.updateIntegration(id, request);
        
        return ResponseEntity.ok(ApiResponse.success(integration, "Integração atualizada"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteIntegration(@PathVariable String id) {
        
        log.info("Deleting integration: {}", id);
        
        integrationService.deleteIntegration(id);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Integração removida"));
    }

    @PostMapping("/{id}/execute")
    public ResponseEntity<ApiResponse<ExecutionLogResponse>> executeIntegration(@PathVariable String id) {
        
        log.info("Executing integration: {}", id);
        
        ExecutionLogResponse executionLog = integrationService.executeIntegration(id);
        
        return ResponseEntity.ok(ApiResponse.success(executionLog, "Integração executada"));
    }

    @GetMapping("/{id}/logs")
    public ResponseEntity<ApiResponse<Page<ExecutionLogResponse>>> getExecutionLogs(
            @PathVariable String id,
            @PageableDefault(size = 50) Pageable pageable) {
        
        log.debug("Getting execution logs for integration: {}", id);
        
        Page<ExecutionLogResponse> logs = integrationService.getExecutionLogs(id, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(logs));
    }
}
