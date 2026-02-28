package com.afadhitya.taskmanagement.application.usecase.feature.handler;

import com.afadhitya.taskmanagement.application.dto.request.InviteMemberRequest;
import com.afadhitya.taskmanagement.application.dto.response.WorkspaceMemberResponse;
import com.afadhitya.taskmanagement.application.port.feature.FeatureContext;
import com.afadhitya.taskmanagement.application.port.feature.FeatureHandler;
import com.afadhitya.taskmanagement.application.port.out.feature.FeatureTogglePort;
import com.afadhitya.taskmanagement.application.port.out.workspace.WorkspacePersistencePort;
import com.afadhitya.taskmanagement.domain.exception.PlanLimitExceededException;
import com.afadhitya.taskmanagement.domain.feature.Feature;
import com.afadhitya.taskmanagement.domain.feature.LimitType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberLimitFeatureHandler implements FeatureHandler<InviteMemberRequest, WorkspaceMemberResponse> {

    private final WorkspacePersistencePort workspacePersistencePort;
    private final FeatureTogglePort featureTogglePort;

    @Override
    public Feature getFeature() {
        return Feature.MEMBER_LIMITS;
    }

    @Override
    public void validate(FeatureContext context, InviteMemberRequest request) {
        Long workspaceId = context.getWorkspaceId();

        int currentMemberCount = workspacePersistencePort.countMembers(workspaceId);
        int maxAllowed = featureTogglePort.getLimit(workspaceId, LimitType.MAX_MEMBERS);

        if (maxAllowed >= 0 && currentMemberCount >= maxAllowed) {
            throw new PlanLimitExceededException(
                LimitType.MAX_MEMBERS,
                currentMemberCount,
                maxAllowed
            );
        }
    }
}
