
package com.totvs.integration.service;

import com.totvs.integration.entity.Integration;
import com.totvs.integration.repository.IntegrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class IntegrationServiceHelper {

    @Autowired
    private IntegrationRepository integrationRepository;

    
    public Page<Integration> findByTenantIdAndTags(String tenantId, List<String> tags, Pageable pageable) {
        if (tags == null || tags.isEmpty()) {
            return integrationRepository.findByTenantId(tenantId, pageable);
        }
        
       
        String tag1 = tags.size() > 0 ? tags.get(0) : null;
        String tag2 = tags.size() > 1 ? tags.get(1) : null;
        String tag3 = tags.size() > 2 ? tags.get(2) : null;
        String tag4 = tags.size() > 3 ? tags.get(3) : null;
        String tag5 = tags.size() > 4 ? tags.get(4) : null;
        
        return integrationRepository.findByTenantIdAndTagsIn(
            tenantId, tag1, tag2, tag3, tag4, tag5, pageable
        );
    }
}