package com.totvs.integration.exception;

public class IntegrationNotFoundException extends RuntimeException {
    public IntegrationNotFoundException(String integrationId) {
        super("Integration not found: " + integrationId);
    }
    
    public IntegrationNotFoundException(String integrationId, String tenantId) {
        super("Integration not found: " + integrationId + " for tenant: " + tenantId);
    }
}
