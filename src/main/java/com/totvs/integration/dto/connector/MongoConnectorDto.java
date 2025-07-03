
package com.totvs.integration.dto.connector;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MongoConnectorDto {
    
    private String connectionString;
    private String database;
    private String collection;
    private String username;
    private String password;
    private String authDatabase;
    private String filter; 
    private Integer limit;
    private String sort; 
}