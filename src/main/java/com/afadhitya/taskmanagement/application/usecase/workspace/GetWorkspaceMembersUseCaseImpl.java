package com.afadhitya.taskmanagement.application.usecase.workspace;

import com.afadhitya.taskmanagement.application.dto.response.WorkspaceMemberResponse;
import com.afadhitya.taskmanagement.application.port.in.workspace.GetWorkspaceMembersUseCase;
import com.afadhitya.taskmanagement.application.port.out.workspace.WorkspaceMemberPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.WorkspaceMember;
import com.afadhitya.taskmanagement.domain.enums.WorkspaceRole;
import com.afadhitya.taskmanagement.domain.exception.WorkspaceAccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetWorkspaceMembersUseCaseImpl implements GetWorkspaceMembersUseCase {

    private final WorkspaceMemberPersistencePort workspaceMemberPersistencePort;

    @Override
    public List<WorkspaceMemberResponse> getWorkspaceMembers(Long workspaceId, Long currentUserId) {
        // Check if user is a member of the workspace AND has ADMIN role
        boolean hasAdminAccess = workspaceMemberPersistencePort.existsByWorkspaceIdAndUserIdAndRole(
                workspaceId, currentUserId, WorkspaceRole.ADMIN);

        if (!hasAdminAccess) {
            throw new WorkspaceAccessDeniedException("Only workspace admins can view members");
        }

        List<WorkspaceMember> members = workspaceMemberPersistencePort.findByWorkspaceId(workspaceId);

        return members.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private WorkspaceMemberResponse mapToResponse(WorkspaceMember member) {
        return WorkspaceMemberResponse.builder()
                .id(member.getId())
                .userId(member.getUser().getId())
                .email(member.getUser().getEmail())
                .fullName(member.getUser().getFullName())
                .avatarUrl(member.getUser().getAvatarUrl())
                .role(member.getRole())
                .invitedById(member.getInvitedBy() != null ? member.getInvitedBy().getId() : null)
                .invitedByName(member.getInvitedBy() != null ? member.getInvitedBy().getFullName() : null)
                .joinedAt(member.getJoinedAt())
                .build();
    }
}
