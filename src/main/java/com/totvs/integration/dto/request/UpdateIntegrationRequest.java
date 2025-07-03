package com.totvs.integration.dto.request;

import com.totvs.integration.entity.ConnectorConfig;
import com.totvs.integration.entity.DataTransformation;
import com.totvs.integration.entity.ScheduleConfig;
import com.totvs.integration.entity.Integration;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateIntegrationRequest {
    
    private String name;
    private String description;
    
    @Valid
    private ConnectorConfig sourceConnector;
    
    @Valid
    private ConnectorConfig targetConnector;
    
    @Valid
    private DataTransformation transformation;
    
    @Valid
    private ScheduleConfig schedule;
    
    private Integration.IntegrationStatus status;
    private Map<String, Object> configuration;
    private List<String> tags;
}
