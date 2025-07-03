// ============================================================================
// IntegrationService.java - COMPLETO CORRIGIDO PARA PostgreSQL/H2
// ============================================================================
package com.totvs.integration.service;

import com.totvs.integration.dto.request.CreateIntegrationRequest;
import com.totvs.integration.dto.request.UpdateIntegrationRequest;
import com.totvs.integration.dto.response.IntegrationResponse;
import com.totvs.integration.dto.response.ExecutionLogResponse;
import com.totvs.integration.entity.Integration;
import com.totvs.integration.entity.ExecutionLog;
import com.totvs.integration.exception.IntegrationNotFoundException;
import com.totvs.integration.repository.IntegrationRepository;
import com.totvs.integration.repository.ExecutionLogRepository;
import com.totvs.integration.security.TenantContext;
import com.totvs.integration.service.IntegrationServiceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class IntegrationService {

    @Autowired
    private IntegrationRepository integrationRepository;
    
    @Autowired
    private ExecutionLogRepository executionLogRepository;

    @Autowired
    private IntegrationServiceHelper integrationServiceHelper;
    
    @Autowired
    private IntegrationExecutorService executorService;

    

    public IntegrationResponse createIntegration(CreateIntegrationRequest request) {
        String tenantId = TenantContext.getCurrentTenantId();
        
        log.info("Creating integration '{}' for tenant '{}'", request.getName(), tenantId);
        
        Integration integration = Integration.builder()
                .tenantId(tenantId)
                .name(request.getName())
                .description(request.getDescription())
                .sourceConnector(request.getSourceConnector())
                .targetConnector(request.getTargetConnector())
                .transformation(request.getTransformation())
                .schedule(request.getSchedule())
                .status(Integration.IntegrationStatus.DRAFT)
                .executionCount(0)
                .errorCount(0)
                .successCount(0)
                .configuration(request.getConfiguration())
                .tags(request.getTags())
                .build();
        
        integration = integrationRepository.save(integration);
        
        log.info("Integration '{}' created with ID: {}", integration.getName(), integration.getId());
        
        return toIntegrationResponse(integration);
    }


  public Page<IntegrationResponse> listIntegrations(Pageable pageable, String status, String name, List<String> tags) {
        String tenantId = TenantContext.getCurrentTenantId();
        
        log.debug("Listing integrations for tenant '{}' with filters - status: {}, name: {}, tags: {}", 
                tenantId, status, name, tags);
        
        Page<Integration> integrations;
        
        if (StringUtils.hasText(status)) {
            Integration.IntegrationStatus statusEnum = Integration.IntegrationStatus.valueOf(status.toUpperCase());
            integrations = integrationRepository.findByTenantIdAndStatus(tenantId, statusEnum, pageable);
        } else if (StringUtils.hasText(name)) {
            integrations = integrationRepository.findByTenantIdAndNameContaining(tenantId, name, pageable);
        } else if (tags != null && !tags.isEmpty()) {
            

            
            log.debug("Filtering integrations by tags: {}", tags);
            
            integrations = integrationServiceHelper.findByTenantIdAndTags(tenantId, tags, pageable);
            
            log.debug("Found {} integrations matching tags filter", integrations.getTotalElements());
        } else {
            integrations = integrationRepository.findByTenantId(tenantId, pageable);
        }
        
        return integrations.map(this::toIntegrationResponse);
    }


    @Cacheable(value = "integrations", key = "#id + '_' + T(com.totvs.integration.security.TenantContext).getCurrentTenantId()")
    public IntegrationResponse getIntegration(String id) {
        String tenantId = TenantContext.getCurrentTenantId();
        
        Integration integration = integrationRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IntegrationNotFoundException(id, tenantId));
        
        return toIntegrationResponse(integration);
    }

    

    @CacheEvict(value = "integrations", key = "#id + '_' + T(com.totvs.integration.security.TenantContext).getCurrentTenantId()")
    public IntegrationResponse updateIntegration(String id, UpdateIntegrationRequest request) {
        String tenantId = TenantContext.getCurrentTenantId();
        
        log.info("Updating integration '{}' for tenant '{}'", id, tenantId);
        
        Integration integration = integrationRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IntegrationNotFoundException(id, tenantId));
        
       
        if (StringUtils.hasText(request.getName())) {
            integration.setName(request.getName());
        }
        if (StringUtils.hasText(request.getDescription())) {
            integration.setDescription(request.getDescription());
        }
        if (request.getSourceConnector() != null) {
            integration.setSourceConnector(request.getSourceConnector());
        }
        if (request.getTargetConnector() != null) {
            integration.setTargetConnector(request.getTargetConnector());
        }
        if (request.getTransformation() != null) {
            integration.setTransformation(request.getTransformation());
        }
        if (request.getSchedule() != null) {
            integration.setSchedule(request.getSchedule());
        }
        if (request.getStatus() != null) {
            integration.setStatus(request.getStatus());
        }
        if (request.getConfiguration() != null) {
            integration.setConfiguration(request.getConfiguration());
        }
        if (request.getTags() != null) {
            integration.setTags(request.getTags());
        }
        
        integration = integrationRepository.save(integration);
        
        log.info("Integration '{}' updated successfully", id);
        
        return toIntegrationResponse(integration);
    }

   

    @CacheEvict(value = "integrations", key = "#id + '_' + T(com.totvs.integration.security.TenantContext).getCurrentTenantId()")
    public void deleteIntegration(String id) {
        String tenantId = TenantContext.getCurrentTenantId();
        
        log.info("Deleting integration '{}' for tenant '{}'", id, tenantId);
        
        Integration integration = integrationRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IntegrationNotFoundException(id, tenantId));
        
        integrationRepository.delete(integration);
        
        log.info("Integration '{}' deleted successfully", id);
    }

    

    public ExecutionLogResponse executeIntegration(String id) {
        String tenantId = TenantContext.getCurrentTenantId();
        
        log.info("Executing integration '{}' for tenant '{}'", id, tenantId);
        
        Integration integration = integrationRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IntegrationNotFoundException(id, tenantId));
        
       
        if (integration.getStatus() != Integration.IntegrationStatus.ACTIVE) {
            log.warn("Integration '{}' is not active, current status: {}", id, integration.getStatus());
        }
        
       
        ExecutionLog executionLog = ExecutionLog.builder()
                .tenantId(tenantId)
                .integrationId(id)
                .integrationName(integration.getName())
                .startTime(LocalDateTime.now())
                .status(ExecutionLog.ExecutionStatus.STARTED)
                .sourceConnectorType(integration.getSourceConnector() != null ? integration.getSourceConnector().getType() : "UNKNOWN")
                .targetConnectorType(integration.getTargetConnector() != null ? integration.getTargetConnector().getType() : "UNKNOWN")
                .executionId(UUID.randomUUID().toString())
                .build();
        
        executionLog = executionLogRepository.save(executionLog);
        
        try {
      
            executorService.executeIntegrationAsync(integration);
            
     
            integration.setLastExecution(LocalDateTime.now());
            integration.incrementExecution();
            
           
            integration.setStatus(Integration.IntegrationStatus.RUNNING);
            integrationRepository.save(integration);
            
        } catch (Exception e) {
            log.error("Error starting execution for integration '{}': {}", id, e.getMessage(), e);
            integration.recordError(e.getMessage());
            integrationRepository.save(integration);
            
            
            executionLog.setStatus(ExecutionLog.ExecutionStatus.FAILED);
            executionLog.setErrorMessage(e.getMessage());
            executionLog.setEndTime(LocalDateTime.now());
            executionLogRepository.save(executionLog);
        }
        
        return toExecutionLogResponse(executionLog);
    }

  

    public Page<ExecutionLogResponse> getExecutionLogs(String integrationId, Pageable pageable) {
        String tenantId = TenantContext.getCurrentTenantId();
        
     
        integrationRepository.findByIdAndTenantId(integrationId, tenantId)
                .orElseThrow(() -> new IntegrationNotFoundException(integrationId, tenantId));
        
        Page<ExecutionLog> logs = executionLogRepository.findByIntegrationIdOrderByCreatedAtDesc(integrationId, pageable);
        
        return logs.map(this::toExecutionLogResponse);
    }

  

    @CacheEvict(value = "integrations", key = "#id + '_' + T(com.totvs.integration.security.TenantContext).getCurrentTenantId()")
    public IntegrationResponse changeStatus(String id, String status) {
        String tenantId = TenantContext.getCurrentTenantId();
        
        log.info("Changing status of integration '{}' to '{}' for tenant '{}'", id, status, tenantId);
        
        Integration integration = integrationRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new IntegrationNotFoundException(id, tenantId));
        
        try {
            Integration.IntegrationStatus newStatus = Integration.IntegrationStatus.valueOf(status.toUpperCase());
            integration.setStatus(newStatus);
            
           
            if (newStatus == Integration.IntegrationStatus.ACTIVE) {
                integration.setLastError(null);
            }
            
            integration = integrationRepository.save(integration);
            
            log.info("Integration '{}' status changed to '{}' successfully", id, status);
            
        } catch (IllegalArgumentException e) {
            log.error("Invalid status '{}' for integration '{}'", status, id);
            throw new IllegalArgumentException("Invalid status: " + status);
        }
        
        return toIntegrationResponse(integration);
    }

    
    public long countIntegrationsByTenant(String tenantId) {
        return integrationRepository.countByTenantId(tenantId);
    }

    public long countActiveIntegrationsByTenant(String tenantId) {
        return integrationRepository.countByTenantIdAndStatus(tenantId, Integration.IntegrationStatus.ACTIVE);
    }

    public List<Integration> findIntegrationsToExecute() {
        return integrationRepository.findIntegrationsToExecute(LocalDateTime.now());
    }

    public List<Integration> findIntegrationsWithErrors(String tenantId) {
        return integrationRepository.findWithRecentErrors(tenantId);
    }

   

    private IntegrationResponse toIntegrationResponse(Integration integration) {
        return IntegrationResponse.builder()
                .id(integration.getId())
                .tenantId(integration.getTenantId())
                .name(integration.getName())
                .description(integration.getDescription())
                .sourceConnector(integration.getSourceConnector())
                .targetConnector(integration.getTargetConnector())
                .transformation(integration.getTransformation())
                .schedule(integration.getSchedule())
                .status(integration.getStatus())
                .lastExecution(integration.getLastExecution())
                .nextExecution(integration.getNextExecution())
                .executionCount(integration.getExecutionCount())
                .errorCount(integration.getErrorCount())
                .successCount(integration.getSuccessCount())
                .lastError(integration.getLastError())
                .configuration(integration.getConfiguration())
                .tags(integration.getTags())
                .createdAt(integration.getCreatedAt())
                .updatedAt(integration.getUpdatedAt())
                .build();
    }

    private ExecutionLogResponse toExecutionLogResponse(ExecutionLog log) {
        return ExecutionLogResponse.builder()
                .id(log.getId())
                .integrationId(log.getIntegrationId())
                .integrationName(log.getIntegrationName())
                .startTime(log.getStartTime())
                .endTime(log.getEndTime())
                .durationMs(log.getDurationMs())
                .status(log.getStatus())
                .errorMessage(log.getErrorMessage())
                .recordsProcessed(log.getRecordsProcessed())
                .recordsSuccess(log.getRecordsSuccess())
                .recordsFailed(log.getRecordsFailed())
                .recordsSkipped(log.getRecordsSkipped())
                .sourceConnectorType(log.getSourceConnectorType())
                .targetConnectorType(log.getTargetConnectorType())
                .sourceReadTimeMs(log.getSourceReadTimeMs())
                .transformationTimeMs(log.getTransformationTimeMs())
                .targetWriteTimeMs(log.getTargetWriteTimeMs())
                .sourceStats(log.getSourceStats())
                .targetStats(log.getTargetStats())
                .metadata(log.getMetadata())
                .executionId(log.getExecutionId())
                .createdAt(log.getCreatedAt())
                .build();
    }
}