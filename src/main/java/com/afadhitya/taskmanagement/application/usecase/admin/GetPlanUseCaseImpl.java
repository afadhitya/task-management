package com.afadhitya.taskmanagement.application.usecase.admin;

import com.afadhitya.taskmanagement.adapter.out.feature.PlanConfigurationEntity;
import com.afadhitya.taskmanagement.adapter.out.feature.PlanFeatureEntity;
import com.afadhitya.taskmanagement.application.dto.response.admin.PlanDetailResponse;
import com.afadhitya.taskmanagement.application.dto.response.admin.PlanFeatureResponse;
import com.afadhitya.taskmanagement.application.dto.response.admin.PlanLimitResponse;
import com.afadhitya.taskmanagement.application.port.in.admin.GetPlanUseCase;
import com.afadhitya.taskmanagement.application.port.out.admin.AdminPlanPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetPlanUseCaseImpl implements GetPlanUseCase {

    private final AdminPlanPersistencePort adminPlanPersistencePort;

    @Override
    public PlanDetailResponse getPlan(Long planId) {
        PlanConfigurationEntity plan = adminPlanPersistencePort.findPlanById(planId)
            .orElseThrow(() -> new IllegalArgumentException("Plan not found: " + planId));

        List<PlanFeatureEntity> planFeatures = adminPlanPersistencePort.findPlanFeaturesByPlanId(planId);

        List<PlanFeatureResponse> features = planFeatures.stream()
            .map(pf -> PlanFeatureResponse.builder()
                .code(pf.getFeature().getCode())
                .name(pf.getFeature().getName())
                .category(pf.getFeature().getCategory())
                .isEnabled(pf.getIsEnabled())
                .build())
            .collect(Collectors.toList());

        List<PlanLimitResponse> limits = plan.getPlanLimits().stream()
            .map(pl -> PlanLimitResponse.builder()
                .type(pl.getLimitType())
                .value(pl.getLimitValue())
                .build())
            .collect(Collectors.toList());

        return PlanDetailResponse.builder()
            .id(plan.getId())
            .planTier(plan.getPlanTier())
            .name(plan.getName())
            .description(plan.getDescription())
            .isActive(plan.getIsActive())
            .isDefault(plan.getIsDefault())
            .features(features)
            .limits(limits)
            .build();
    }
}
