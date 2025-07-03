
package com.totvs.integration.config;

import com.totvs.integration.security.TenantInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    @Autowired(required = false) 
    private TenantInterceptor tenantInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (tenantInterceptor != null) {
            logger.info("Registrando TenantInterceptor - multi-tenancy habilitado");
            registry.addInterceptor(tenantInterceptor)
                    .addPathPatterns("/api/**")
                    
                    .excludePathPatterns(
                        "/actuator/**",         
                        "/api/actuator/**",     
                        "/api/swagger-ui/**",                      
                        "/api/v3/api-docs/**",
                        "/swagger-ui/**",       
                        "/v3/api-docs/**"      
                    );
        } else {
            logger.info("TenantInterceptor não disponível - multi-tenancy desabilitado");
        }
    }
}