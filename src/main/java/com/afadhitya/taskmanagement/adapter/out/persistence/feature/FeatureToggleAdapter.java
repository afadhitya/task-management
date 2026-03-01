package com.afadhitya.taskmanagement.adapter.out.persistence.feature;

import com.afadhitya.taskmanagement.adapter.out.persistence.PlanConfigurationRepository;
import com.afadhitya.taskmanagement.adapter.out.persistence.WorkspaceRepository;
import com.afadhitya.taskmanagement.application.port.out.feature.FeatureTogglePort;
import com.afadhitya.taskmanagement.domain.feature.Feature;
import com.afadhitya.taskmanagement.domain.feature.LimitType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeatureToggleAdapter implements FeatureTogglePort {

    private final PlanConfigurationRepository planConfigurationRepository;
    private final WorkspaceRepository workspaceRepository;

    @Override
    @Cacheable(value = "workspaceFeatures", key = "#workspaceId + ':' + #feature.getCode()")
    public boolean isEnabled(Long workspaceId, Feature feature) {
        log.debug("Checking feature {} for workspace {}", feature.getCode(), workspaceId);

        Long planConfigId = findPlanConfigurationId(workspaceId);
        if (planConfigId == null) {
            log.warn("No plan configuration found for workspace {}, defaulting feature {} to false",
                workspaceId, feature.getCode());
            return false;
        }

        return planConfigurationRepository.isFeatureEnabled(planConfigId, feature.getCode())
            .orElse(false);
    }

    @Override
    @Cacheable(value = "workspaceLimits", key = "#workspaceId + ':' + #limitType.getCode()")
    public int getLimit(Long workspaceId, LimitType limitType) {
        log.debug("Getting limit {} for workspace {}", limitType.getCode(), workspaceId);

        Long planConfigId = findPlanConfigurationId(workspaceId);
        if (planConfigId == null) {
            log.warn("No plan configuration found for workspace {}, defaulting limit {} to 0",
                workspaceId, limitType.getCode());
            return 0;
        }

        return planConfigurationRepository.getLimit(planConfigId, limitType.getCode())
            .orElse(0);
    }

    @Override
    @CacheEvict(value = {"workspaceFeatures", "workspaceLimits"}, allEntries = true)
    public void invalidateCache(Long workspaceId) {
        log.info("Invalidating feature cache for workspace {}", workspaceId);
        // Cache eviction handled by Spring annotation
    }

    private Long findPlanConfigurationId(Long workspaceId) {
        return workspaceRepository.findPlanConfigurationIdById(workspaceId);
    }
}
