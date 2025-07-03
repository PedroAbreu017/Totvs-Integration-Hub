
package com.totvs.integration.dto.connector;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConnectorValidationDto {
    
    private Boolean valid;
    private List<String> errors;
    private List<String> warnings;
    private String message;
    private String connectorType;
    private Long validationTimeMs;
}