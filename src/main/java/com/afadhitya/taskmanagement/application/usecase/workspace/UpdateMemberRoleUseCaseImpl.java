package com.afadhitya.taskmanagement.application.usecase.workspace;

import com.afadhitya.taskmanagement.application.dto.request.UpdateMemberRoleRequest;
import com.afadhitya.taskmanagement.application.dto.response.WorkspaceMemberResponse;
import com.afadhitya.taskmanagement.application.port.in.workspace.UpdateMemberRoleUseCase;
import com.afadhitya.taskmanagement.application.port.out.workspace.WorkspaceMemberPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.WorkspaceMember;
import com.afadhitya.taskmanagement.domain.enums.WorkspaceRole;
import com.afadhitya.taskmanagement.domain.exception.WorkspaceAccessDeniedException;
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
        // Check if current user has ADMIN or OWNER role
        WorkspaceMember currentUserMembership = workspaceMemberPersistencePort
                .findByWorkspaceIdAndUserId(workspaceId, currentUserId)
                .orElseThrow(() -> new WorkspaceAccessDeniedException(
                        "You are not a member of this workspace"));

        if (currentUserMembership.getRole() != WorkspaceRole.ADMIN &&
            currentUserMembership.getRole() != WorkspaceRole.OWNER) {
            throw new WorkspaceAccessDeniedException(
                    "Only admins and owners can update member roles");
        }

        // Prevent changing role to OWNER
        if (request.role() == WorkspaceRole.OWNER) {
            throw new IllegalArgumentException(
                    "Cannot change member role to OWNER. Transfer ownership instead.");
        }

        // Find the target member
        WorkspaceMember targetMember = workspaceMemberPersistencePort
                .findByWorkspaceIdAndUserId(workspaceId, userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Member not found in this workspace"));

        // Prevent modifying the OWNER's role
        if (targetMember.getRole() == WorkspaceRole.OWNER) {
            throw new IllegalArgumentException(
                    "Cannot modify the workspace owner's role");
        }

        // Update the role
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
