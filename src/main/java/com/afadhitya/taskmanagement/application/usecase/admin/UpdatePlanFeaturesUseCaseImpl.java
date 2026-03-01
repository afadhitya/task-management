package com.afadhitya.taskmanagement.application.usecase.admin;

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
        for (UpdateFeaturesRequest.FeatureToggleRequest toggle : request.features()) {
            int updated = adminPlanPersistencePort.updateFeatureStatus(planId, toggle.code(), toggle.isEnabled());
            if (updated == 0) {
                throw new IllegalArgumentException("Feature not found: " + toggle.code());
            }
        }
    }
}
