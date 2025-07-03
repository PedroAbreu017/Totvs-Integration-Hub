package com.totvs.integration.dto.response;

import com.totvs.integration.entity.ConnectorConfig;
import com.totvs.integration.entity.DataTransformation;
import com.totvs.integration.entity.ScheduleConfig;
import com.totvs.integration.entity.Integration;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntegrationResponse {
    
    private String id;
    private String tenantId;
    private String name;
    private String description;
    
    
    private ConnectorConfig sourceConnector;
    private ConnectorConfig targetConnector;
    
    
    private DataTransformation transformation;
    private ScheduleConfig schedule;
    
   
    private Integration.IntegrationStatus status;
    private LocalDateTime lastExecution;
    private LocalDateTime nextExecution;
    
    
    private Integer executionCount;
    private Integer errorCount;
    private Integer successCount;  
    
    
    private String lastError; 
    
   
    private Map<String, Object> configuration;
    private List<String> tags;
    
  
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
   
    public double getSuccessRate() {
        if (executionCount == null || executionCount == 0) {
            return 0.0;
        }
        int success = successCount != null ? successCount : 0;
        return (double) success / executionCount * 100.0;
    }
    
    public boolean isActive() {
        return Integration.IntegrationStatus.ACTIVE.equals(status);
    }
    
    public boolean hasErrors() {
        return errorCount != null && errorCount > 0;
    }
}