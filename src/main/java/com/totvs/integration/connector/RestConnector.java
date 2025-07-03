package com.totvs.integration.connector;

import com.totvs.integration.entity.ConnectorConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

@Slf4j
@Component
public class RestConnector implements ConnectorHandler {

    private final RestTemplate restTemplate;

    public RestConnector() {
        this.restTemplate = new RestTemplate();
    }

    
    
    
    @Override
    public boolean testConnection(Map<String, Object> config) {
        try {
            ResponseEntity<String> response = get(config, "/health");
            boolean isSuccess = response.getStatusCode().is2xxSuccessful();
            log.info("Teste de conexão REST: {}", isSuccess ? "SUCESSO" : "FALHA");
            return isSuccess;
            
        } catch (Exception e) {
            log.error("Erro ao testar conexão REST: {}", e.getMessage());
            return false;
        }
    }

   
     
    
    public ResponseEntity<String> get(Map<String, Object> config, String endpoint) {
        String baseUrl = (String) config.get("baseUrl");
        String fullUrl = baseUrl + (endpoint != null ? endpoint : "");
        
        log.info("Executando GET: {}", fullUrl);
        
        try {
            HttpHeaders headers = buildHeaders(config);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            return restTemplate.exchange(fullUrl, HttpMethod.GET, entity, String.class);
            
        } catch (Exception e) {
            log.error("Erro ao executar GET: {}", e.getMessage());
            throw new RuntimeException("Erro na requisição GET", e);
        }
    }

    
    
    
    private HttpHeaders buildHeaders(Map<String, Object> config) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
       
        @SuppressWarnings("unchecked")
        Map<String, String> customHeaders = (Map<String, String>) config.get("headers");
        if (customHeaders != null) {
            customHeaders.forEach(headers::add);
        }
        
       
        String authType = (String) config.get("authType");
        if (authType != null) {
            switch (authType.toUpperCase()) {
                case "BASIC":
                    String username = (String) config.get("username");
                    String password = (String) config.get("password");
                    String auth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
                    headers.add("Authorization", "Basic " + auth);
                    break;
                    
                case "BEARER":
                case "TOKEN":
                    String token = (String) config.get("token");
                    headers.add("Authorization", "Bearer " + token);
                    break;
                    
                case "API_KEY":
                    String apiKey = (String) config.get("apiKey");
                    headers.add("X-API-Key", apiKey);
                    break;
            }
        }
        
        return headers;
    }

    
  
     
    public ResponseEntity<String> get(ConnectorConfig config, String endpoint) {
        return get(config.getConfiguration(), endpoint);
    }
}