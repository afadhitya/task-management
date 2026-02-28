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
    private final FeatureRepository featureRepository;
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
    public Optional<FeatureEntity> findFeatureByCode(String code) {
        return featureRepository.findByCode(code);
    }

    @Override
    public Optional<PlanFeatureEntity> findPlanFeature(Long planConfigurationId, Long featureId) {
        return planFeatureRepository.findByPlanConfigurationIdAndFeatureId(planConfigurationId, featureId);
    }

    @Override
    public void savePlanFeature(PlanFeatureEntity planFeature) {
        planFeatureRepository.save(planFeature);
    }

    @Override
    public Optional<PlanLimitEntity> findPlanLimit(Long planConfigurationId, String limitType) {
        return planLimitRepository.findByPlanConfigurationIdAndLimitType(planConfigurationId, limitType);
    }

    @Override
    public void savePlanLimit(PlanLimitEntity planLimit) {
        planLimitRepository.save(planLimit);
    }
}
