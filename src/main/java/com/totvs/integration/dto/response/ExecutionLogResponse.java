package com.totvs.integration.dto.response;

import com.totvs.integration.entity.ExecutionLog;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExecutionLogResponse {
    
    private String id;
    private String integrationId;
    private String integrationName;
    
    
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long durationMs;
    
   
    private ExecutionLog.ExecutionStatus status;
    private String errorMessage;
    private String stackTrace;
    
    
    private Integer recordsProcessed;
    private Integer recordsSuccess;
    private Integer recordsFailed;
    private Integer recordsSkipped;
    
   
    private String sourceConnectorType;
    private String targetConnectorType;
    
    
    private Long sourceReadTimeMs;
    private Long transformationTimeMs;
    private Long targetWriteTimeMs;
    
  
    private Map<String, Object> sourceStats;  
    private Map<String, Object> targetStats;  
    private Map<String, Object> metadata;
    
   
    private String executionId;
    private LocalDateTime createdAt;
    
    
    public boolean isSuccess() {
        return ExecutionLog.ExecutionStatus.SUCCESS.equals(status);
    }
    
    public boolean isFailed() {
        return ExecutionLog.ExecutionStatus.FAILED.equals(status);
    }
    
    public boolean isRunning() {
        return ExecutionLog.ExecutionStatus.RUNNING.equals(status) || 
               ExecutionLog.ExecutionStatus.STARTED.equals(status);
    }
    
    public double getSuccessRate() {
        if (recordsProcessed == null || recordsProcessed == 0) {
            return 0.0;
        }
        int success = recordsSuccess != null ? recordsSuccess : 0;
        return (double) success / recordsProcessed * 100.0;
    }
}