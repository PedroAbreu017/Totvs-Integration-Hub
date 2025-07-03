
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
public class WebhookConnectorDto {
    
    private String url;
    private String method; 
    private Map<String, String> headers;
    private String authType;
    private String authToken;
    private String contentType;
    private Integer timeoutSeconds;
    private Integer maxRetries;
    private String payloadTemplate;
}