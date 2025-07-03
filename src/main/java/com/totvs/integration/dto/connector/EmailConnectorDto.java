
package com.totvs.integration.dto.connector;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailConnectorDto {
    
    private String smtpHost;
    private Integer smtpPort;
    private String username;
    private String password;
    private Boolean sslEnabled;
    private Boolean tlsEnabled;
    private String fromAddress;
    private String fromName;
    private String[] toAddresses;
    private String[] ccAddresses;
    private String[] bccAddresses;
    private String subject;
    private String bodyTemplate;
}