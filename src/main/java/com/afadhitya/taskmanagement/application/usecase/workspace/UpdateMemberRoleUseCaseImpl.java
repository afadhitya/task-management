package com.afadhitya.taskmanagement.application.usecase.workspace;

import com.afadhitya.taskmanagement.application.dto.request.UpdateMemberRoleRequest;
import com.afadhitya.taskmanagement.application.dto.response.WorkspaceMemberResponse;
import com.afadhitya.taskmanagement.application.port.in.workspace.UpdateMemberRoleUseCase;
import com.afadhitya.taskmanagement.application.port.out.workspace.WorkspaceMemberPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.WorkspaceMember;
import com.afadhitya.taskmanagement.domain.enums.WorkspaceRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateMemberRoleUseCaseImpl implements UpdateMemberRoleUseCase {

    private final WorkspaceMemberPersistencePort workspaceMemberPersistencePort;

    @Override
    public WorkspaceMemberResponse updateMemberRole(Long workspaceId, Long userId, UpdateMemberRoleRequest request, Long currentUserId) {
        if (request.role() == WorkspaceRole.OWNER) {
            throw new IllegalArgumentException(
                    "Cannot change member role to OWNER. Transfer ownership instead.");
        }

        WorkspaceMember targetMember = workspaceMemberPersistencePort
                .findByWorkspaceIdAndUserId(workspaceId, userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Member not found in this workspace"));

        if (targetMember.getRole() == WorkspaceRole.OWNER) {
            throw new IllegalArgumentException(
                    "Cannot modify the workspace owner's role");
        }

        targetMember.setRole(request.role());
        WorkspaceMember updatedMember = workspaceMemberPersistencePort.save(targetMember);

        return mapToResponse(updatedMember);
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
