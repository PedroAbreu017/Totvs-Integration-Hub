
package com.totvs.integration.dto.connector;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Map;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConnectorSchemaDto {
    
    private String type;
    private String title;
    private String description;
    private Map<String, Object> properties;
    private List<String> required;
    private Map<String, Object> examples;
    private String version;
}
