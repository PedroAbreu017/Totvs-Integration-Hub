package com.totvs.integration.connector;

import com.totvs.integration.entity.ConnectorConfig;
import java.util.List;
import java.util.Map;



public interface ConnectorHandler {
    
    

    boolean testConnection(Map<String, Object> config);
    
    

    default Object execute(Map<String, Object> config, Map<String, Object> parameters) {
        throw new UnsupportedOperationException("Operação execute não implementada para este conector");
    }
    
    

    default boolean validateConfig(Map<String, Object> config) {
        return config != null && !config.isEmpty();
    }
    
    

    default List<Map<String, Object>> readData(Map<String, Object> config, Map<String, Object> parameters) {
        throw new UnsupportedOperationException("Operação readData não implementada para este conector");
    }
    
    

    default List<Map<String, Object>> readData(ConnectorConfig config) {
        return readData(config.getConfiguration(), Map.of());
    }
    
    

    default int writeData(Map<String, Object> config, List<Map<String, Object>> data, Map<String, Object> parameters) {
        throw new UnsupportedOperationException("Operação writeData não implementada para este conector");
    }
    
    
    
    default int writeData(ConnectorConfig config, List<Map<String, Object>> data) {
        return writeData(config.getConfiguration(), data, Map.of());
    }
}