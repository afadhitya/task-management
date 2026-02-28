package com.afadhitya.taskmanagement.application.usecase.feature;

import com.afadhitya.taskmanagement.application.dto.request.InviteMemberRequest;
import com.afadhitya.taskmanagement.application.dto.response.WorkspaceMemberResponse;
import com.afadhitya.taskmanagement.application.port.feature.FeatureContext;
import com.afadhitya.taskmanagement.application.port.in.workspace.InviteMemberUseCase;
import com.afadhitya.taskmanagement.application.usecase.audit.AuditedWorkspaceUseCases;
import com.afadhitya.taskmanagement.application.usecase.feature.handler.MemberLimitFeatureHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary
@RequiredArgsConstructor
public class InviteMemberFeatureDispatcher implements InviteMemberUseCase {

    private final AuditedWorkspaceUseCases.InviteMember auditedInviteMember;
    private final MemberLimitFeatureHandler limitHandler;

    @Override
    @Transactional
    public WorkspaceMemberResponse inviteMember(Long workspaceId, InviteMemberRequest request, Long currentUserId) {
        FeatureContext context = new FeatureContext(workspaceId, currentUserId);

        limitHandler.validate(context, request);

        return auditedInviteMember.inviteMember(workspaceId, request, currentUserId);
    }
}
