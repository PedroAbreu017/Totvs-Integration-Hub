package com.totvs.integration.connector;

import com.totvs.integration.connector.DatabaseConnector;
import com.totvs.integration.connector.EmailConnector;
import com.totvs.integration.connector.FileConnector;
import com.totvs.integration.connector.RestConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ConnectorFactory {

    private static final Logger logger = LoggerFactory.getLogger(ConnectorFactory.class);

   
    
     
    public ConnectorHandler getConnector(String type) {
        try {
            switch (type.toUpperCase()) {
                case "DATABASE_POSTGRESQL":
                case "DATABASE_MYSQL":
                case "DATABASE_ORACLE":
                case "DATABASE_SQLSERVER":
                    return new DatabaseConnector();
                    
                case "REST_API":
                    return new RestConnector();
                    
                case "EMAIL_SMTP":
                    return new EmailConnector();
                    
                case "FILE_CSV":
                case "FILE_JSON":
                case "FILE_XML":
                    return new FileConnector();
                    
                case "MONGODB":
                    return new DatabaseConnector(); // Fallback
                    
                case "WEBHOOK":
                    return new RestConnector(); // Webhook usa REST
                    
                default:
                    logger.warn("Tipo de conector não suportado: {}", type);
                    return null;
            }
        } catch (Exception e) {
            logger.error("Erro ao criar conector do tipo {}: {}", type, e.getMessage());
            return null;
        }
    }

    
    
    
    public ConnectorHandler createConnector(String type) {
        return getConnector(type);
    }

    
     
     
    public List<String> validateConfigWithErrors(String type, Map<String, Object> config) {
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

    

    public boolean validateConfig(String type, Map<String, Object> config) {
        List<String> errors = validateConfigWithErrors(type, config);
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

    
    
    public List<String> getAvailableTypes() {
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
    }
}