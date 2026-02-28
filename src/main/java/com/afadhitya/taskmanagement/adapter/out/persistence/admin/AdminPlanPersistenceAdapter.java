package com.afadhitya.taskmanagement.adapter.out.persistence.admin;

import com.afadhitya.taskmanagement.adapter.out.feature.*;
import com.afadhitya.taskmanagement.application.port.out.admin.AdminPlanPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AdminPlanPersistenceAdapter implements AdminPlanPersistencePort {

    private final PlanConfigurationRepository planConfigRepository;
    private final PlanFeatureRepository planFeatureRepository;
    private final PlanLimitRepository planLimitRepository;

    @Override
    public List<PlanConfigurationEntity> findAllPlans() {
        return planConfigRepository.findAll();
    }

    @Override
    public Optional<PlanConfigurationEntity> findPlanById(Long id) {
        return planConfigRepository.findById(id);
    }

    @Override
    public List<PlanFeatureEntity> findPlanFeaturesByPlanId(Long planId) {
        return planFeatureRepository.findByPlanConfigurationId(planId);
    }

    @Override
    public int updateFeatureStatus(Long planId, String featureCode, Boolean isEnabled) {
        return planFeatureRepository.updateFeatureStatus(planId, featureCode, isEnabled);
    }

    @Override
    public int updateLimitValue(Long planId, String limitType, Integer limitValue) {
        return planLimitRepository.updateLimitValue(planId, limitType, limitValue);
    }
}
