
package com.totvs.integration.dto.connector;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileConnectorDto {
    
    private String filePath;
    private String fileType; 
    private String encoding; 
    private String delimiter; 
    private Boolean hasHeader; 
    private String sheetName;
    private String ftpHost; 
    private Integer ftpPort;
    private String ftpUsername;
    private String ftpPassword;
}