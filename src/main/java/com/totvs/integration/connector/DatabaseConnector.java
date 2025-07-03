package com.totvs.integration.connector;

import com.totvs.integration.entity.ConnectorConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

@Slf4j
@Component
public class DatabaseConnector implements ConnectorHandler {

    

    @Override
    public boolean testConnection(Map<String, Object> config) {
        String type = (String) config.get("type");
        if (type == null) {
            type = "DATABASE_POSTGRESQL"; 
        }
        
        switch (type.toUpperCase()) {
            case "DATABASE_POSTGRESQL":
                return testPostgreSQLConnection(config);
            case "DATABASE_MYSQL":
                return testMySQLConnection(config);
            default:
                log.warn("Tipo de banco não suportado: {}", type);
                return false;
        }
    }

    

    public Connection connectPostgreSQL(Map<String, Object> config) throws SQLException {
        String host = (String) config.get("host");
        Integer port = (Integer) config.getOrDefault("port", 5432);
        String database = (String) config.get("database");
        
        String url = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
        
        String username = (String) config.get("username");
        String password = (String) config.get("password");
        
        log.info("Conectando ao PostgreSQL: {}:{}/{}", host, port, database);
        
        return DriverManager.getConnection(url, username, password);
    }

    

    public Connection connectPostgreSQL(ConnectorConfig config) throws SQLException {
        return connectPostgreSQL(config.getConfiguration());
    }

    

    public Connection connectMySQL(Map<String, Object> config) throws SQLException {
        String host = (String) config.get("host");
        Integer port = (Integer) config.getOrDefault("port", 3306);
        String database = (String) config.get("database");
        
        String url = String.format("jdbc:mysql://%s:%d/%s", host, port, database);
        
        String username = (String) config.get("username");
        String password = (String) config.get("password");
        
        log.info("Conectando ao MySQL: {}:{}/{}", host, port, database);
        
        return DriverManager.getConnection(url, username, password);
    }

    

    public Connection connectMySQL(ConnectorConfig config) throws SQLException {
        return connectMySQL(config.getConfiguration());
    }

   

    public boolean testPostgreSQLConnection(Map<String, Object> config) {
        try (Connection conn = connectPostgreSQL(config)) {
            boolean isValid = conn.isValid(5); // 5 segundos timeout
            log.info("Teste de conexão PostgreSQL: {}", isValid ? "SUCESSO" : "FALHA");
            return isValid;
        } catch (SQLException e) {
            log.error("Erro ao testar conexão PostgreSQL: {}", e.getMessage());
            return false;
        }
    }

    
    
    public boolean testMySQLConnection(Map<String, Object> config) {
        try (Connection conn = connectMySQL(config)) {
            boolean isValid = conn.isValid(5);
            log.info("Teste de conexão MySQL: {}", isValid ? "SUCESSO" : "FALHA");
            return isValid;
        } catch (SQLException e) {
            log.error("Erro ao testar conexão MySQL: {}", e.getMessage());
            return false;
        }
    }
}