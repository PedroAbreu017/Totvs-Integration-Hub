
package com.totvs.integration.repository;

import com.totvs.integration.entity.Integration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Component
public class IntegrationRepositoryWrapper {

    @Autowired
    private IntegrationRepository repository;

    
    public Page<Integration> findByTenantIdAndTagsIn(String tenantId, List<String> tags, Pageable pageable) {
        if (tags == null || tags.isEmpty()) {
            return repository.findByTenantId(tenantId, pageable);
        }
        
        
        String tag1 = tags.size() > 0 ? tags.get(0) : null;
        String tag2 = tags.size() > 1 ? tags.get(1) : null;
        String tag3 = tags.size() > 2 ? tags.get(2) : null;
        String tag4 = tags.size() > 3 ? tags.get(3) : null;
        String tag5 = tags.size() > 4 ? tags.get(4) : null;
        
        return repository.findByTenantIdAndTagsIn(tenantId, tag1, tag2, tag3, tag4, tag5, pageable);
    }


    public Page<Integration> findByTenantIdAndStatus(String tenantId, Integration.IntegrationStatus status, Pageable pageable) {
        return repository.findByTenantIdAndStatus(tenantId, status, pageable);
    }

    public Page<Integration> findByTenantIdAndNameContaining(String tenantId, String name, Pageable pageable) {
        return repository.findByTenantIdAndNameContaining(tenantId, name, pageable);
    }

    public Page<Integration> findByTenantId(String tenantId, Pageable pageable) {
        return repository.findByTenantId(tenantId, pageable);
    }

    public Optional<Integration> findByIdAndTenantId(String id, String tenantId) {
        return repository.findByIdAndTenantId(id, tenantId);
    }

    public List<Integration> findByTenantId(String tenantId) {
        return repository.findByTenantId(tenantId);
    }

    public List<Integration> findByTenantIdAndStatus(String tenantId, Integration.IntegrationStatus status) {
        return repository.findByTenantIdAndStatus(tenantId, status);
    }

    public long countByTenantId(String tenantId) {
        return repository.countByTenantId(tenantId);
    }

    public long countByTenantIdAndStatus(String tenantId, Integration.IntegrationStatus status) {
        return repository.countByTenantIdAndStatus(tenantId, status);
    }


    public Integration save(Integration integration) {
        return repository.save(integration);
    }

    public Optional<Integration> findById(String id) {
        return repository.findById(id);
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }

    public boolean existsById(String id) {
        return repository.existsById(id);
    }

    public List<Integration> findAll() {
        return repository.findAll();
    }

    public void delete(Integration integration) {
        repository.delete(integration);
    }
}