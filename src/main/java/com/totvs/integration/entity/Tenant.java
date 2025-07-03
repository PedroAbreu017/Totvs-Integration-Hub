package com.totvs.integration.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tenants", indexes = {
    @Index(name = "idx_tenant_domain", columnList = "domain"),
    @Index(name = "idx_tenant_tenant_id", columnList = "tenant_id"),
    @Index(name = "idx_tenant_api_key", columnList = "api_key")
})
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String domain;

    @Column(nullable = false)
    private String email;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "tenant_id", unique = true)
    private String tenantId;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @Enumerated(EnumType.STRING)
    private Plan plan = Plan.FREE;


    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "settings", columnDefinition = "TEXT")
    @Basic(fetch = FetchType.LAZY)
    private Map<String, Object> settings = new HashMap<>();

    @Column(name = "max_requests_per_minute")
    private Integer maxRequestsPerMinute = 1000;

    @Column(name = "max_concurrent_integrations")
    private Integer maxConcurrentIntegrations = 10;

    @Column(name = "api_key", unique = true)
    private String apiKey;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Embedded
    private TenantUsage usage = new TenantUsage();

    

    public String getContactEmail() {
        return contactEmail != null ? contactEmail : email;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
        if (this.email == null) {
            this.email = contactEmail;
        }
    }

    public String getTenantId() {
        return this.tenantId != null ? this.tenantId : this.domain;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
        if (this.domain == null) {
            this.domain = tenantId;
        }
    }
    
    
    
    public void setSettings(Map<String, Object> settings) {
        if (settings == null) {
            this.settings = new HashMap<>();
        } else {
            this.settings = new HashMap<>(settings);
        }
    }
    
    public Map<String, Object> getSettings() {
        if (this.settings == null) {
            this.settings = new HashMap<>();
        }
        return this.settings;
    }
    
    public void setSetting(String key, Object value) {
        if (this.settings == null) {
            this.settings = new HashMap<>();
        }
        this.settings.put(key, value);
    }
    
    public Object getSetting(String key) {
        if (this.settings == null) {
            return null;
        }
        return this.settings.get(key);
    }
    
    public String getSettingAsString(String key) {
        Object value = getSetting(key);
        return value != null ? value.toString() : null;
    }

    public Integer getSettingAsInteger(String key) {
        Object value = getSetting(key);
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value instanceof String) {
            try {
                return Integer.valueOf((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public Boolean getSettingAsBoolean(String key) {
        Object value = getSetting(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof String) {
            return Boolean.valueOf((String) value);
        }
        return null;
    }

    

    public enum Status {
        ACTIVE("Active"),
        INACTIVE("Inactive"),
        SUSPENDED("Suspended"),
        PENDING("Pending");

        private final String displayName;

        Status(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum Plan {
        FREE("Free", 100, 5),
        BASIC("Basic", 1000, 25),
        PROFESSIONAL("Professional", 10000, 100),
        ENTERPRISE("Enterprise", -1, -1);

        private final String displayName;
        private final int maxRequestsPerMinute;
        private final int maxConcurrentIntegrations;

        Plan(String displayName, int maxRequestsPerMinute, int maxConcurrentIntegrations) {
            this.displayName = displayName;
            this.maxRequestsPerMinute = maxRequestsPerMinute;
            this.maxConcurrentIntegrations = maxConcurrentIntegrations;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getMaxRequestsPerMinute() {
            return maxRequestsPerMinute;
        }

        public int getMaxConcurrentIntegrations() {
            return maxConcurrentIntegrations;
        }

        public boolean isUnlimited() {
            return this == ENTERPRISE;
        }
    }

   

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TenantUsage {
        @Column(name = "total_integrations")
        private Long totalIntegrations = 0L;
        
        @Column(name = "active_integrations")
        private Long activeIntegrations = 0L;
        
        @Column(name = "total_executions")
        private Long totalExecutions = 0L;
        
        @Column(name = "current_month_executions")
        private Long currentMonthExecutions = 0L;
        
        @Column(name = "success_rate")
        private Double successRate = 0.0;
        
        @Column(name = "current_month_api_calls")
        private Long currentMonthApiCalls = 0L;
    }



    public boolean isActive() {
        return Status.ACTIVE.equals(this.status);
    }

    public boolean canMakeRequests() {
        return isActive() && !Status.SUSPENDED.equals(this.status);
    }

    @PreUpdate
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (usage == null) {
            usage = new TenantUsage();
        }
        if (settings == null) {
            settings = new HashMap<>();
        }
    }

    public void applyPlanLimits() {
        if (plan != null && !plan.isUnlimited()) {
            this.maxRequestsPerMinute = plan.getMaxRequestsPerMinute();
            this.maxConcurrentIntegrations = plan.getMaxConcurrentIntegrations();
        }
    }

    public boolean isValid() {
        return name != null && !name.trim().isEmpty() &&
               domain != null && !domain.trim().isEmpty() &&
               email != null && !email.trim().isEmpty() &&
               email.contains("@");
    }

    public void generateApiKey() {
        if (this.domain != null) {
            this.apiKey = "tk_" + this.domain.replaceAll("[^a-zA-Z0-9]", "") + "_" +
                         System.currentTimeMillis();
        }
    }
}