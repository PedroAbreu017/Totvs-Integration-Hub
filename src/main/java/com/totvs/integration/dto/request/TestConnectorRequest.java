package com.totvs.integration.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestConnectorRequest {

    @NotBlank(message = "Tipo do conector é obrigatório")
    private String type;

    @NotNull(message = "Configuração é obrigatória")
    private Map<String, Object> config;

    private Map<String, Object> parameters;

    private String description;

    
    public Map<String, Object> getConfiguration() {
        return this.config;
    }

    public void setConfiguration(Map<String, Object> configuration) {
        this.config = configuration;
    }
}