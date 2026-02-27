package com.afadhitya.taskmanagement.application.usecase.workspace;

import com.afadhitya.taskmanagement.application.port.in.workspace.RemoveMemberUseCase;
import com.afadhitya.taskmanagement.application.port.out.workspace.WorkspaceMemberPersistencePort;
import com.afadhitya.taskmanagement.application.port.out.workspace.WorkspacePersistencePort;
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
        if (userId.equals(currentUserId)) {
            throw new IllegalArgumentException("Cannot remove yourself from the workspace. Use leave workspace instead.");
        }

        WorkspaceMember currentUserMembership = workspaceMemberPersistencePort
                .findByWorkspaceIdAndUserId(workspaceId, currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("You are not a member of this workspace"));

        WorkspaceRole currentUserRole = currentUserMembership.getRole();

        WorkspaceMember targetMember = workspaceMemberPersistencePort
                .findByWorkspaceIdAndUserId(workspaceId, userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Member not found in this workspace"));

        WorkspaceRole targetRole = targetMember.getRole();

        if (targetRole == WorkspaceRole.OWNER) {
            throw new IllegalArgumentException("Cannot remove the workspace owner");
        }

        if (currentUserRole == WorkspaceRole.ADMIN && targetRole == WorkspaceRole.ADMIN) {
            throw new WorkspaceAccessDeniedException(
                    "Admins cannot remove other admins. Only the owner can do that.");
        }

        workspaceMemberPersistencePort.delete(targetMember);
    }
}
