package com.totvs.integration.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectorTestResponse {
    
    private boolean success;
    private String message;
    private Long responseTimeMs;  
    private Date testTimestamp;
    private Map<String, Object> testResults;
    private String errorDetails;

    
    public Long getResponseTime() {
        return responseTimeMs;
    }
    
    public void setResponseTime(Long responseTime) {
        this.responseTimeMs = responseTime;
    }
}