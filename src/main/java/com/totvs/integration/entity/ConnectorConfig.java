package com.totvs.integration.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "connector_configs", indexes = {
    @Index(name = "idx_connector_name", columnList = "name"),
    @Index(name = "idx_connector_type", columnList = "type"),
    @Index(name = "idx_connector_tenant", columnList = "tenant_id")
})
public class ConnectorConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(columnDefinition = "TEXT")
    private String description;

    


    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "configuration", columnDefinition = "TEXT")
    @JsonProperty("configuration")
    private Map<String, Object> configuration = new HashMap<>();

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    

    @PreUpdate
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        if (this.updatedAt == null) {
            this.updatedAt = now;
        }
        
        
        if (this.configuration == null) {
            this.configuration = new HashMap<>();
        }
    }

    
    
   
    
    public boolean isValid() {
        return name != null && !name.trim().isEmpty() &&
               type != null && !type.trim().isEmpty() &&
               tenantId != null && !tenantId.trim().isEmpty();
    }
}