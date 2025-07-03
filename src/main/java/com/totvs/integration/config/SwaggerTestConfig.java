// ============================================================================
// SWAGGER TEST CONFIGURATION - DESABILITA SWAGGER EM TESTES
// ============================================================================
package com.totvs.integration.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Configuration
@Profile("test")
@ConditionalOnProperty(name = "swagger.enabled", havingValue = "false", matchIfMissing = true)
public class SwaggerTestConfig {
    
  
}