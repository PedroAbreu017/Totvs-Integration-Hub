
package com.totvs.integration.dto.connector;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatabaseConnectorDto {
    
    private String host;
    private Integer port;
    private String database;
    private String username;
    private String password;
    private String driverType; 
    private Boolean sslEnabled;
    private Integer connectionPoolSize;
    private String schema;
    private String tableName;
    private String customQuery;
}