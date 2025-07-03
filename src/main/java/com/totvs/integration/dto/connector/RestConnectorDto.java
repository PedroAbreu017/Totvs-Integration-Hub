
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
public class RestConnectorDto {
    
    private String endpoint;
    private String method;
    private Map<String, String> headers;
    private String authType;
    private String username;
    private String password;
    private String authToken;
    private Integer timeoutSeconds;
    private Boolean sslEnabled;
}
