package com.afadhitya.taskmanagement.application.port.out.admin;

import com.afadhitya.taskmanagement.domain.feature.PlanConfigurationEntity;
import com.afadhitya.taskmanagement.domain.feature.PlanFeatureEntity;

import java.util.List;
import java.util.Optional;

public interface AdminPlanPersistencePort {

    List<PlanConfigurationEntity> findAllPlans();

    Optional<PlanConfigurationEntity> findPlanById(Long id);

    List<PlanFeatureEntity> findPlanFeaturesByPlanId(Long planId);

    int updateFeatureStatus(Long planId, String featureCode, Boolean isEnabled);

    int updateLimitValue(Long planId, String limitType, Integer limitValue);
}
