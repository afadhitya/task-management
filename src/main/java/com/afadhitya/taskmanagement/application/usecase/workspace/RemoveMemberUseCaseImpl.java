package com.afadhitya.taskmanagement.application.usecase.workspace;

import com.afadhitya.taskmanagement.application.port.in.workspace.RemoveMemberUseCase;
import com.afadhitya.taskmanagement.application.port.out.workspace.WorkspaceMemberPersistencePort;
import com.afadhitya.taskmanagement.application.port.out.workspace.WorkspacePersistencePort;
import com.afadhitya.taskmanagement.domain.entity.Workspace;
import com.afadhitya.taskmanagement.domain.entity.WorkspaceMember;
import com.afadhitya.taskmanagement.domain.enums.WorkspaceRole;
import com.afadhitya.taskmanagement.domain.exception.WorkspaceAccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RemoveMemberUseCaseImpl implements RemoveMemberUseCase {

    private final WorkspacePersistencePort workspacePersistencePort;
    private final WorkspaceMemberPersistencePort workspaceMemberPersistencePort;

    @Override
    public void removeMember(Long workspaceId, Long userId, Long currentUserId) {
        // Verify workspace exists
        Workspace workspace = workspacePersistencePort.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found with id: " + workspaceId));

        // Check if current user is trying to remove themselves
        if (userId.equals(currentUserId)) {
            throw new IllegalArgumentException("Cannot remove yourself from the workspace. Use leave workspace instead.");
        }

        // Get current user's membership
        WorkspaceMember currentUserMembership = workspaceMemberPersistencePort
                .findByWorkspaceIdAndUserId(workspaceId, currentUserId)
                .orElseThrow(() -> new WorkspaceAccessDeniedException(
                        "You are not a member of this workspace"));

        WorkspaceRole currentUserRole = currentUserMembership.getRole();

        // Only ADMIN and OWNER can remove members
        if (currentUserRole != WorkspaceRole.ADMIN && currentUserRole != WorkspaceRole.OWNER) {
            throw new WorkspaceAccessDeniedException(
                    "Only admins and owners can remove members from the workspace");
        }

        // Get target member's membership
        WorkspaceMember targetMember = workspaceMemberPersistencePort
                .findByWorkspaceIdAndUserId(workspaceId, userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Member not found in this workspace"));

        WorkspaceRole targetRole = targetMember.getRole();

        // OWNER can never be deleted
        if (targetRole == WorkspaceRole.OWNER) {
            throw new IllegalArgumentException("Cannot remove the workspace owner");
        }

        // If current user is ADMIN, can only delete GUEST and MEMBER
        if (currentUserRole == WorkspaceRole.ADMIN) {
            if (targetRole == WorkspaceRole.ADMIN) {
                throw new WorkspaceAccessDeniedException(
                        "Admins cannot remove other admins. Only the owner can do that.");
            }
        }

        // OWNER can delete anyone (ADMIN, MEMBER, GUEST) - no additional check needed

        // Remove the member
        workspaceMemberPersistencePort.delete(targetMember);
    }
}
