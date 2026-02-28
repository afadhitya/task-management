package com.afadhitya.taskmanagement.application.usecase.entitlement;

import com.afadhitya.taskmanagement.adapter.out.feature.PlanConfigurationEntity;
import com.afadhitya.taskmanagement.adapter.out.feature.PlanConfigurationRepository;
import com.afadhitya.taskmanagement.adapter.out.persistence.ProjectRepository;
import com.afadhitya.taskmanagement.adapter.out.persistence.WorkspaceMemberRepository;
import com.afadhitya.taskmanagement.adapter.out.persistence.WorkspaceRepository;
import com.afadhitya.taskmanagement.application.dto.response.WorkspaceEntitlementResponse;
import com.afadhitya.taskmanagement.application.port.in.entitlement.GetWorkspaceEntitlementsUseCase;
import com.afadhitya.taskmanagement.application.port.out.feature.FeatureTogglePort;
import com.afadhitya.taskmanagement.domain.entity.Workspace;
import com.afadhitya.taskmanagement.domain.feature.Feature;
import com.afadhitya.taskmanagement.domain.feature.LimitType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetWorkspaceEntitlementsUseCaseImpl implements GetWorkspaceEntitlementsUseCase {

    private final WorkspaceRepository workspaceRepository;
    private final PlanConfigurationRepository planConfigRepository;
    private final ProjectRepository projectRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final FeatureTogglePort featureTogglePort;

    @Override
    public WorkspaceEntitlementResponse getEntitlements(Long workspaceId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
            .orElseThrow(() -> new IllegalArgumentException("Workspace not found: " + workspaceId));

        PlanConfigurationEntity plan = planConfigRepository.findById(workspace.getPlanConfigurationId())
            .orElseThrow(() -> new IllegalArgumentException("Plan configuration not found"));

        List<WorkspaceEntitlementResponse.FeatureInfo> features = Arrays.stream(Feature.values())
            .map(f -> WorkspaceEntitlementResponse.FeatureInfo.builder()
                .code(f.getCode())
                .name(f.name())
                .isEnabled(featureTogglePort.isEnabled(workspaceId, f))
                .build())
            .collect(Collectors.toList());

        List<WorkspaceEntitlementResponse.LimitInfo> limits = Arrays.asList(
            buildLimitInfo(workspaceId, LimitType.MAX_PROJECTS, projectRepository.countByWorkspaceId(workspaceId)),
            buildLimitInfo(workspaceId, LimitType.MAX_MEMBERS, workspaceMemberRepository.countByWorkspaceId(workspaceId))
        );

        return WorkspaceEntitlementResponse.builder()
            .workspaceId(workspaceId)
            .plan(WorkspaceEntitlementResponse.PlanInfo.builder()
                .tier(plan.getPlanTier())
                .name(plan.getName())
                .build())
            .features(features)
            .limits(limits)
            .build();
    }

    private WorkspaceEntitlementResponse.LimitInfo buildLimitInfo(Long workspaceId, LimitType limitType, int used) {
        int limit = featureTogglePort.getLimit(workspaceId, limitType);
        int remaining = limit < 0 ? -1 : Math.max(0, limit - used);

        return WorkspaceEntitlementResponse.LimitInfo.builder()
            .type(limitType.getCode())
            .limit(limit)
            .used(used)
            .remaining(remaining)
            .build();
    }
}
