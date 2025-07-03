package com.totvs.integration.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "integrations", indexes = {
    @Index(name = "idx_integration_tenant", columnList = "tenant_id"),
    @Index(name = "idx_integration_status", columnList = "status"),
    @Index(name = "idx_integration_next_exec", columnList = "next_execution")
})
public class Integration {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "source_connector", columnDefinition = "TEXT")
    private ConnectorConfig sourceConnector;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "target_connector", columnDefinition = "TEXT")
    private ConnectorConfig targetConnector;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "transformation", columnDefinition = "TEXT")
    private DataTransformation transformation;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "schedule", columnDefinition = "TEXT")
    private ScheduleConfig schedule;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "configuration", columnDefinition = "TEXT")
    private Map<String, Object> configuration;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tags", columnDefinition = "TEXT")
    private List<String> tags;
    
    
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private IntegrationStatus status = IntegrationStatus.DRAFT;
    
    @Column(name = "last_execution")
    private LocalDateTime lastExecution;
    
    @Column(name = "next_execution")
    private LocalDateTime nextExecution;
    
    @Column(name = "execution_count")
    @Builder.Default
    private Integer executionCount = 0;
    
    @Column(name = "error_count")
    @Builder.Default
    private Integer errorCount = 0;
    
    @Column(name = "success_count")
    @Builder.Default
    private Integer successCount = 0;
    
    @Column(name = "last_error", columnDefinition = "TEXT")
    private String lastError;
    
    
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (executionCount == null) {
            executionCount = 0;
        }
        if (errorCount == null) {
            errorCount = 0;
        }
        if (successCount == null) {
            successCount = 0;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    

    
    public enum IntegrationStatus {
        DRAFT("Draft"),
        ACTIVE("Active"),
        INACTIVE("Inactive"),
        ERROR("Error"),
        RUNNING("Running"),
        PAUSED("Paused"),
        MAINTENANCE("Maintenance");
        
        private final String description;
        
        IntegrationStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
   
    
    public boolean hasTag(String tag) {
        return tags != null && tags.contains(tag);
    }
    
    public void addTag(String tag) {
        if (tags == null) {
            tags = new java.util.ArrayList<>();
        }
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }
    
    public void removeTag(String tag) {
        if (tags != null) {
            tags.remove(tag);
        }
    }
    
    public void incrementExecution() {
        this.executionCount = (this.executionCount != null ? this.executionCount : 0) + 1;
        this.lastExecution = LocalDateTime.now();
    }
    
    public void recordSuccess() {
        incrementExecution();
        this.successCount = (this.successCount != null ? this.successCount : 0) + 1;
        this.lastError = null;
        this.status = IntegrationStatus.ACTIVE;
    }
    
    public void recordError(String error) {
        incrementExecution();
        this.errorCount = (this.errorCount != null ? this.errorCount : 0) + 1;
        this.lastError = error;
        this.status = IntegrationStatus.ERROR;
    }
    
    public boolean isExecutable() {
        return status == IntegrationStatus.ACTIVE && 
               (nextExecution == null || nextExecution.isBefore(LocalDateTime.now()));
    }
    
    public void scheduleNext(LocalDateTime nextExecution) {
        this.nextExecution = nextExecution;
    }
    
    public double getSuccessRate() {
        if (executionCount == null || executionCount == 0) {
            return 0.0;
        }
        int success = successCount != null ? successCount : 0;
        return (double) success / executionCount * 100.0;
    }
}