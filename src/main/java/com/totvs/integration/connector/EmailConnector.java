// src/main/java/com/totvs/integration/connector/EmailConnector.java
package com.totvs.integration.connector;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class EmailConnector implements ConnectorHandler {
    
    @Override
    public boolean testConnection(Map<String, Object> config) {
        log.info("Testando conexão SMTP");
        
        return true;
    }
}

