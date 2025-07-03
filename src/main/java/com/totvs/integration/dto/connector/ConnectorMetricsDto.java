
package com.totvs.integration.dto.connector;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConnectorMetricsDto {
    
    private String connectorType;
    private Long totalConnections;
    private Long successfulConnections;
    private Long failedConnections;
    private Double successRate;
    private Long averageResponseTimeMs;
    private LocalDateTime lastUsed;
    private String tenantId;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
}