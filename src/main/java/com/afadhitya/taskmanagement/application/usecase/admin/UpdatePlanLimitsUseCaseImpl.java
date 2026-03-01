package com.afadhitya.taskmanagement.application.usecase.admin;

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
        for (UpdateLimitsRequest.LimitValueRequest limitReq : request.limits()) {
            int updated = adminPlanPersistencePort.updateLimitValue(planId, limitReq.type(), limitReq.value());
            if (updated == 0) {
                throw new IllegalArgumentException("Limit not found: " + limitReq.type());
            }
        }
    }
}
