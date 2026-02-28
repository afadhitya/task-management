package com.afadhitya.taskmanagement.application.usecase.admin;

import com.afadhitya.taskmanagement.adapter.out.feature.PlanConfigurationEntity;
import com.afadhitya.taskmanagement.adapter.out.feature.PlanLimitEntity;
import com.afadhitya.taskmanagement.application.dto.request.admin.UpdateLimitsRequest;
import com.afadhitya.taskmanagement.application.port.in.admin.UpdatePlanLimitsUseCase;
import com.afadhitya.taskmanagement.application.port.out.admin.AdminPlanPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdatePlanLimitsUseCaseImpl implements UpdatePlanLimitsUseCase {

    private final AdminPlanPersistencePort adminPlanPersistencePort;

    @Override
    @Transactional
    public void updateLimits(Long planId, UpdateLimitsRequest request) {
        PlanConfigurationEntity plan = adminPlanPersistencePort.findPlanById(planId)
            .orElseThrow(() -> new IllegalArgumentException("Plan not found: " + planId));

        for (UpdateLimitsRequest.LimitValueRequest limitReq : request.limits()) {
            PlanLimitEntity planLimit = adminPlanPersistencePort
                .findPlanLimit(planId, limitReq.type())
                .orElseThrow(() -> new IllegalArgumentException("Plan limit not found: " + limitReq.type()));

            planLimit.setLimitValue(limitReq.value());
            adminPlanPersistencePort.savePlanLimit(planLimit);
        }
    }
}
