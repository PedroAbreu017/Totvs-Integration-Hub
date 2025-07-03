package com.totvs.integration.exception;

public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException(String tenantId, int limit) {
        super("Rate limit exceeded for tenant: " + tenantId + ". Limit: " + limit + " requests per minute");
    }
}
