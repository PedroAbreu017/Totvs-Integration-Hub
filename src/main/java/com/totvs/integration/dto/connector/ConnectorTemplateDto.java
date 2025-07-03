
package com.totvs.integration.dto.connector;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConnectorTemplateDto {
    
    private String type;
    private String name;
    private String description;
    private String category; 
    private Map<String, Object> defaultConfig;
    private Map<String, Object> schema;
    private String[] requiredFields;
    private String[] optionalFields;
    private String documentation;
    private String iconUrl;
}
