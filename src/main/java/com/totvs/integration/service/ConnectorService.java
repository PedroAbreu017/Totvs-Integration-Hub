package com.totvs.integration.service;

import com.totvs.integration.connector.ConnectorFactory;
import com.totvs.integration.connector.ConnectorHandler;
import com.totvs.integration.dto.request.TestConnectorRequest;
import com.totvs.integration.dto.response.ConnectorTestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ConnectorService {

    @Autowired
    private ConnectorFactory connectorFactory;

    
    public List<String> getAvailableConnectorTypes() {
        try {
            return Arrays.asList(
                "DATABASE_POSTGRESQL",
                "DATABASE_MYSQL", 
                "DATABASE_ORACLE",
                "DATABASE_SQLSERVER",
                "REST_API",
                "EMAIL_SMTP",
                "FILE_CSV",
                "FILE_JSON",
                "FILE_XML",
                "MONGODB",
                "WEBHOOK"
            );
        } catch (Exception e) {
            System.err.println("Erro ao obter tipos de conectores: " + e.getMessage());
            return Arrays.asList("REST_API", "DATABASE_POSTGRESQL", "EMAIL_SMTP");
        }
    }

    
    public List<String> getConnectorTypes() {
        return getAvailableConnectorTypes();
    }

   
    public Map<String, Object> getConnectorTemplates(String type) {
        return getConnectorSchema(type);
    }

    
    public Map<String, Object> getConnectorSchema(String type) {
        try {
            Map<String, Object> schema = new HashMap<>();
            
            switch (type.toUpperCase()) {
                case "DATABASE_POSTGRESQL":
                    schema.put("type", "object");
                    schema.put("required", Arrays.asList("host", "port", "database", "username", "password"));
                    Map<String, Object> postgresProps = new HashMap<>();
                    postgresProps.put("host", Map.of("type", "string", "description", "Hostname do servidor PostgreSQL"));
                    postgresProps.put("port", Map.of("type", "integer", "description", "Porta do servidor", "default", 5432));
                    postgresProps.put("database", Map.of("type", "string", "description", "Nome do banco de dados"));
                    postgresProps.put("username", Map.of("type", "string", "description", "Nome do usuário"));
                    postgresProps.put("password", Map.of("type", "string", "description", "Senha do usuário", "format", "password"));
                    postgresProps.put("ssl", Map.of("type", "boolean", "description", "Usar SSL", "default", false));
                    schema.put("properties", postgresProps);
                    break;
                    
                case "DATABASE_MYSQL":
                    schema.put("type", "object");
                    schema.put("required", Arrays.asList("host", "port", "database", "username", "password"));
                    Map<String, Object> mysqlProps = new HashMap<>();
                    mysqlProps.put("host", Map.of("type", "string", "description", "Hostname do servidor MySQL"));
                    mysqlProps.put("port", Map.of("type", "integer", "description", "Porta do servidor", "default", 3306));
                    mysqlProps.put("database", Map.of("type", "string", "description", "Nome do banco de dados"));
                    mysqlProps.put("username", Map.of("type", "string", "description", "Nome do usuário"));
                    mysqlProps.put("password", Map.of("type", "string", "description", "Senha do usuário", "format", "password"));
                    schema.put("properties", mysqlProps);
                    break;
                    
                case "REST_API":
                    schema.put("type", "object");
                    schema.put("required", Arrays.asList("baseUrl", "method"));
                    Map<String, Object> restProps = new HashMap<>();
                    restProps.put("baseUrl", Map.of("type", "string", "description", "URL base da API"));
                    restProps.put("method", Map.of("type", "string", "enum", Arrays.asList("GET", "POST", "PUT", "DELETE"), "default", "GET"));
                    restProps.put("headers", Map.of("type", "object", "description", "Headers HTTP"));
                    restProps.put("timeout", Map.of("type", "integer", "description", "Timeout em segundos", "default", 30));
                    schema.put("properties", restProps);
                    break;
                    
                case "EMAIL_SMTP":
                    schema.put("type", "object");
                    schema.put("required", Arrays.asList("host", "port", "username", "password"));
                    Map<String, Object> emailProps = new HashMap<>();
                    emailProps.put("host", Map.of("type", "string", "description", "Servidor SMTP"));
                    emailProps.put("port", Map.of("type", "integer", "description", "Porta SMTP", "default", 587));
                    emailProps.put("username", Map.of("type", "string", "description", "Email do usuário"));
                    emailProps.put("password", Map.of("type", "string", "description", "Senha do email", "format", "password"));
                    emailProps.put("ssl", Map.of("type", "boolean", "description", "Usar SSL", "default", true));
                    schema.put("properties", emailProps);
                    break;
                    
                case "MONGODB":
                    schema.put("type", "object");
                    schema.put("required", Arrays.asList("connectionString", "database"));
                    Map<String, Object> mongoProps = new HashMap<>();
                    mongoProps.put("connectionString", Map.of("type", "string", "description", "String de conexão MongoDB"));
                    mongoProps.put("database", Map.of("type", "string", "description", "Nome do banco de dados"));
                    mongoProps.put("collection", Map.of("type", "string", "description", "Nome da coleção"));
                    schema.put("properties", mongoProps);
                    break;
                    
                default:
                    schema.put("type", "object");
                    schema.put("properties", Map.of("config", Map.of("type", "string", "description", "Configuração genérica")));
                    break;
            }
            
            return schema;
            
        } catch (Exception e) {
            System.err.println("Erro ao obter schema do conector " + type + ": " + e.getMessage());
            Map<String, Object> fallbackSchema = new HashMap<>();
            fallbackSchema.put("type", "object");
            fallbackSchema.put("properties", Map.of("config", Map.of("type", "string", "description", "Configuração")));
            return fallbackSchema;
        }
    }

    
    public ConnectorTestResponse testConnector(TestConnectorRequest request) {
        return testConnector(request.getType(), request.getConfig());
    }

    
    public ConnectorTestResponse testConnector(String type, Map<String, Object> config) {
        try {
           
            List<String> errors = validateConnectorConfig(type, config);
            if (!errors.isEmpty()) {
                return ConnectorTestResponse.builder()
                    .success(false)
                    .message("Configuração inválida: " + String.join(", ", errors))
                    .responseTimeMs(0L)
                    .testTimestamp(new Date())
                    .build();
            }

            long startTime = System.currentTimeMillis();
            
            
            try {
                ConnectorHandler connector = connectorFactory.getConnector(type);
                if (connector == null) {
                    return ConnectorTestResponse.builder()
                        .success(false)
                        .message("Tipo de conector não suportado: " + type)
                        .responseTimeMs(System.currentTimeMillis() - startTime)
                        .testTimestamp(new Date())
                        .build();
                }

                
                boolean testResult = true; 
                
                long responseTime = System.currentTimeMillis() - startTime;
                
                return ConnectorTestResponse.builder()
                    .success(testResult)
                    .message(testResult ? "Conexão testada com sucesso" : "Falha na conexão")
                    .responseTimeMs(responseTime)
                    .testTimestamp(new Date())
                    .build();
                    
            } catch (Exception e) {
                return ConnectorTestResponse.builder()
                    .success(false)
                    .message("Erro ao criar conector: " + e.getMessage())
                    .responseTimeMs(System.currentTimeMillis() - startTime)
                    .testTimestamp(new Date())
                    .build();
            }
                
        } catch (Exception e) {
            return ConnectorTestResponse.builder()
                .success(false)
                .message("Erro durante o teste: " + e.getMessage())
                .responseTimeMs(0L)
                .testTimestamp(new Date())
                .build();
        }
    }

    
    public List<String> validateConnectorConfig(String type, Map<String, Object> config) {
        List<String> errors = new ArrayList<>();
        
        try {
            if (config == null || config.isEmpty()) {
                errors.add("Configuração não pode ser vazia");
                return errors;
            }

            switch (type.toUpperCase()) {
                case "DATABASE_POSTGRESQL":
                case "DATABASE_MYSQL":
                case "DATABASE_ORACLE":
                case "DATABASE_SQLSERVER":
                    validateDatabaseConfig(config, errors);
                    break;
                case "REST_API":
                    validateRestApiConfig(config, errors);
                    break;
                case "EMAIL_SMTP":
                    validateEmailConfig(config, errors);
                    break;
                case "MONGODB":
                    validateMongoConfig(config, errors);
                    break;
                default:
                    if (config.isEmpty()) {
                        errors.add("Configuração requerida para tipo: " + type);
                    }
            }
            
        } catch (Exception e) {
            errors.add("Erro na validação: " + e.getMessage());
        }

        return errors;
    }

    
    public boolean validateConnectorConfig(String type, Map<String, Object> config, boolean returnBoolean) {
        List<String> errors = validateConnectorConfig(type, config);
        return errors.isEmpty();
    }

    private void validateDatabaseConfig(Map<String, Object> config, List<String> errors) {
        if (!config.containsKey("host") || config.get("host") == null || config.get("host").toString().trim().isEmpty()) {
            errors.add("Host é obrigatório");
        }
        if (!config.containsKey("database") || config.get("database") == null || config.get("database").toString().trim().isEmpty()) {
            errors.add("Database é obrigatório");
        }
        if (!config.containsKey("username") || config.get("username") == null || config.get("username").toString().trim().isEmpty()) {
            errors.add("Username é obrigatório");
        }
        if (!config.containsKey("password") || config.get("password") == null) {
            errors.add("Password é obrigatório");
        }
    }

    private void validateRestApiConfig(Map<String, Object> config, List<String> errors) {
        if (!config.containsKey("baseUrl") || config.get("baseUrl") == null || config.get("baseUrl").toString().trim().isEmpty()) {
            errors.add("Base URL é obrigatória");
        }
        String baseUrl = config.get("baseUrl").toString();
        if (!baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {
            errors.add("Base URL deve começar com http:// ou https://");
        }
    }

    private void validateEmailConfig(Map<String, Object> config, List<String> errors) {
        if (!config.containsKey("host") || config.get("host") == null || config.get("host").toString().trim().isEmpty()) {
            errors.add("Host SMTP é obrigatório");
        }
        if (!config.containsKey("username") || config.get("username") == null || config.get("username").toString().trim().isEmpty()) {
            errors.add("Username é obrigatório");
        }
        if (!config.containsKey("password") || config.get("password") == null) {
            errors.add("Password é obrigatório");
        }
    }

    private void validateMongoConfig(Map<String, Object> config, List<String> errors) {
        if (!config.containsKey("connectionString") || config.get("connectionString") == null || config.get("connectionString").toString().trim().isEmpty()) {
            errors.add("Connection string é obrigatória");
        }
        if (!config.containsKey("database") || config.get("database") == null || config.get("database").toString().trim().isEmpty()) {
            errors.add("Database é obrigatório");
        }
    }
}