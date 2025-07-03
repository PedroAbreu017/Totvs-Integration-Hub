package com.totvs.integration.dto.request;

import com.totvs.integration.entity.ConnectorConfig;
import com.totvs.integration.entity.DataTransformation;
import com.totvs.integration.entity.ScheduleConfig;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateIntegrationRequest {
    
    @NotBlank(message = "Nome é obrigatório")
    private String name;
    
    private String description;
    
    @NotNull(message = "Conector de origem é obrigatório")
    @Valid
    private ConnectorConfig sourceConnector;
    
    @NotNull(message = "Conector de destino é obrigatório")
    @Valid
    private ConnectorConfig targetConnector;
    
    @Valid
    private DataTransformation transformation;
    
    @Valid
    private ScheduleConfig schedule;
    
    private Map<String, Object> configuration;
    private List<String> tags;
}

