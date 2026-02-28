package com.afadhitya.taskmanagement.application.usecase.admin;

import com.afadhitya.taskmanagement.adapter.out.feature.PlanConfigurationEntity;
import com.afadhitya.taskmanagement.application.dto.response.admin.PlanSummaryResponse;
import com.afadhitya.taskmanagement.application.port.in.admin.ListPlansUseCase;
import com.afadhitya.taskmanagement.application.port.out.admin.AdminPlanPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListPlansUseCaseImpl implements ListPlansUseCase {

    private final AdminPlanPersistencePort adminPlanPersistencePort;

    @Override
    public List<PlanSummaryResponse> listPlans() {
        return adminPlanPersistencePort.findAllPlans().stream()
            .map(this::toSummaryResponse)
            .collect(Collectors.toList());
    }

    private PlanSummaryResponse toSummaryResponse(PlanConfigurationEntity plan) {
        return PlanSummaryResponse.builder()
            .id(plan.getId())
            .planTier(plan.getPlanTier())
            .name(plan.getName())
            .description(plan.getDescription())
            .isActive(plan.getIsActive())
            .isDefault(plan.getIsDefault())
            .featureCount(plan.getPlanFeatures().size())
            .limitCount(plan.getPlanLimits().size())
            .build();
    }
}
