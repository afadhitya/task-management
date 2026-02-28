package com.afadhitya.taskmanagement.application.usecase.admin;

import com.afadhitya.taskmanagement.adapter.out.feature.FeatureEntity;
import com.afadhitya.taskmanagement.adapter.out.feature.PlanConfigurationEntity;
import com.afadhitya.taskmanagement.adapter.out.feature.PlanFeatureEntity;
import com.afadhitya.taskmanagement.application.dto.request.admin.UpdateFeaturesRequest;
import com.afadhitya.taskmanagement.application.port.in.admin.UpdatePlanFeaturesUseCase;
import com.afadhitya.taskmanagement.application.port.out.admin.AdminPlanPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdatePlanFeaturesUseCaseImpl implements UpdatePlanFeaturesUseCase {

    private final AdminPlanPersistencePort adminPlanPersistencePort;

    @Override
    @Transactional
    public void updateFeatures(Long planId, UpdateFeaturesRequest request) {
        PlanConfigurationEntity plan = adminPlanPersistencePort.findPlanById(planId)
            .orElseThrow(() -> new IllegalArgumentException("Plan not found: " + planId));

        for (UpdateFeaturesRequest.FeatureToggleRequest toggle : request.features()) {
            FeatureEntity feature = adminPlanPersistencePort.findFeatureByCode(toggle.code())
                .orElseThrow(() -> new IllegalArgumentException("Feature not found: " + toggle.code()));

            PlanFeatureEntity planFeature = adminPlanPersistencePort
                .findPlanFeature(planId, feature.getId())
                .orElseThrow(() -> new IllegalArgumentException("Plan feature not found"));

            planFeature.setIsEnabled(toggle.isEnabled());
            adminPlanPersistencePort.savePlanFeature(planFeature);
        }
    }
}
