package com.afadhitya.taskmanagement.application.port.out.admin;

import com.afadhitya.taskmanagement.adapter.out.feature.*;

import java.util.List;
import java.util.Optional;

public interface AdminPlanPersistencePort {

    List<PlanConfigurationEntity> findAllPlans();

    Optional<PlanConfigurationEntity> findPlanById(Long id);

    Optional<FeatureEntity> findFeatureByCode(String code);

    Optional<PlanFeatureEntity> findPlanFeature(Long planConfigurationId, Long featureId);

    void savePlanFeature(PlanFeatureEntity planFeature);

    Optional<PlanLimitEntity> findPlanLimit(Long planConfigurationId, String limitType);

    void savePlanLimit(PlanLimitEntity planLimit);
}
