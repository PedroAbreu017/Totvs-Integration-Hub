package com.totvs.integration.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import io.hypersistence.utils.hibernate.type.json.JsonType;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "execution_logs", indexes = {
    @Index(name = "idx_execution_tenant", columnList = "tenant_id"),
    @Index(name = "idx_execution_integration", columnList = "integration_id"),
    @Index(name = "idx_execution_status", columnList = "status"),
    @Index(name = "idx_execution_created", columnList = "created_at")
})
public class ExecutionLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;
    
    @Column(name = "integration_id", nullable = false)
    private String integrationId;
    
    @Column(name = "integration_name")
    private String integrationName;
    
   
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "duration_ms")
    private Long durationMs;
    
    @Enumerated(EnumType.STRING)
    private ExecutionStatus status;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "stack_trace", columnDefinition = "TEXT")
    private String stackTrace;
    
    
    @Column(name = "records_processed")
    private Integer recordsProcessed;
    
    @Column(name = "records_success")
    private Integer recordsSuccess;
    
    @Column(name = "records_failed")
    private Integer recordsFailed;
    
    @Column(name = "records_skipped")
    private Integer recordsSkipped;
    
   
    @Column(name = "source_connector_type")
    private String sourceConnectorType;
    
    @Column(name = "target_connector_type")
    private String targetConnectorType;
    
    
    
    @Type(JsonType.class)
    @Column(name = "source_stats", columnDefinition = "TEXT")
    private Map<String, Object> sourceStats;
    
    @Type(JsonType.class)
    @Column(name = "target_stats", columnDefinition = "TEXT")
    private Map<String, Object> targetStats;
    
    @Type(JsonType.class)
    @Column(name = "metadata", columnDefinition = "TEXT")
    private Map<String, Object> metadata;
    
    
    @Column(name = "source_read_time_ms")
    private Long sourceReadTimeMs;
    
    @Column(name = "transformation_time_ms")
    private Long transformationTimeMs;
    
    @Column(name = "target_write_time_ms")
    private Long targetWriteTimeMs;
    
    @Column(name = "execution_id", unique = true)
    private String executionId; 
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (executionId == null) {
            executionId = java.util.UUID.randomUUID().toString();
        }
    }
    
    public enum ExecutionStatus {
        STARTED, RUNNING, SUCCESS, FAILED, CANCELLED, TIMEOUT
    }
}