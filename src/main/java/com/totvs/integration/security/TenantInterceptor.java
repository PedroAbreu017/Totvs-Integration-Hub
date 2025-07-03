
package com.totvs.integration.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
@ConditionalOnProperty(name = "app.multi-tenant.enabled", havingValue = "true", matchIfMissing = false)
public class TenantInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(TenantInterceptor.class);

    @Value("${app.multi-tenant.header-name:X-Tenant-ID}")
    private String tenantHeaderName;

    @Value("${app.multi-tenant.default-tenant:default}")
    private String defaultTenant;

    @Value("${app.multi-tenant.strict-mode:false}")
    private boolean strictMode;

    @Value("${app.security.api-key.header-name:X-API-Key}")
    private String apiKeyHeaderName;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            String tenantId = extractTenantId(request);
            
            if (tenantId == null || tenantId.trim().isEmpty()) {
                if (strictMode) {
                    logger.warn("Tenant ID não fornecido em modo strict - rejeitando request");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"error\":\"Tenant ID obrigatório\"}");
                    return false;
                } else {
                    tenantId = defaultTenant;
                    logger.debug("Usando tenant padrão: {}", tenantId);
                }
            }

           
            TenantContext.setCurrentTenant(tenantId);
            logger.debug("Tenant definido para request: {}", tenantId);
            
            return true;
            
        } catch (Exception e) {
            logger.error("Erro ao processar tenant interceptor", e);
            return true; 
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                               Object handler, Exception ex) {
        
        TenantContext.clear();
        logger.debug("Contexto de tenant limpo");
    }

  
    private String extractTenantId(HttpServletRequest request) {
        
        String tenantId = request.getHeader(tenantHeaderName);
        
        if (tenantId == null || tenantId.trim().isEmpty()) {
            
            tenantId = request.getParameter("tenant");
        }
        
        if (tenantId == null || tenantId.trim().isEmpty()) {
           
            tenantId = extractTenantFromSubdomain(request);
        }
        
        return tenantId != null ? tenantId.trim() : null;
    }

    
    private String extractTenantFromSubdomain(HttpServletRequest request) {
        String serverName = request.getServerName();
        if (serverName != null && serverName.contains(".")) {
            String[] parts = serverName.split("\\.");
            if (parts.length > 2) {
                return parts[0]; 
            }
        }
        return null;
    }

    
    public static class TenantContext {
        private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

        public static void setCurrentTenant(String tenant) {
            currentTenant.set(tenant);
        }

        public static String getCurrentTenant() {
            return currentTenant.get();
        }

        public static void clear() {
            currentTenant.remove();
        }
    }
}