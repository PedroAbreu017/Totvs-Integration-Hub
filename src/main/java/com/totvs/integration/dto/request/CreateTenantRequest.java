package com.totvs.integration.dto.request;

import com.totvs.integration.entity.Tenant;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.*;
import java.util.Map;
import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTenantRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String name;

    @NotBlank(message = "Domínio é obrigatório") 
    @Pattern(regexp = "^[a-z0-9.-]+$", message = "Domínio deve conter apenas letras minúsculas, números, pontos e hífens")
    private String domain;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Formato de email inválido")
    private String email;

    @Size(max = 500, message = "Descrição não pode exceder 500 caracteres")
    private String description;

    @NotNull(message = "Plano é obrigatório")
    private Tenant.Plan plan = Tenant.Plan.FREE;
    
    private Map<String, Object> settings = new HashMap<>();
    
    @Min(value = 1, message = "Máximo de requisições por minuto deve ser pelo menos 1")
    private Integer maxRequestsPerMinute;
    
    @Min(value = 1, message = "Máximo de integrações concorrentes deve ser pelo menos 1")
    private Integer maxConcurrentIntegrations;

   
    private String tenantId;

    
    public String getTenantId() {
       
        return this.tenantId != null ? this.tenantId : this.domain;
    }
    
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    
        if (this.domain == null) {
            this.domain = tenantId;
        }
    }
}