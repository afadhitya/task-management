package com.afadhitya.taskmanagement.application.usecase.workspace;

import com.afadhitya.taskmanagement.application.port.in.workspace.LeaveWorkspaceUseCase;
import com.afadhitya.taskmanagement.application.port.out.workspace.WorkspaceMemberPersistencePort;
import com.afadhitya.taskmanagement.application.port.out.workspace.WorkspacePersistencePort;
import com.afadhitya.taskmanagement.domain.entity.Workspace;
import com.afadhitya.taskmanagement.domain.entity.WorkspaceMember;
import com.afadhitya.taskmanagement.domain.enums.WorkspaceRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LeaveWorkspaceUseCaseImpl implements LeaveWorkspaceUseCase {

    private final WorkspacePersistencePort workspacePersistencePort;
    private final WorkspaceMemberPersistencePort workspaceMemberPersistencePort;

    @Override
    public void leaveWorkspace(Long workspaceId, Long currentUserId) {
        // Verify workspace exists
        workspacePersistencePort.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found with id: " + workspaceId));

        // Get current user's membership
        WorkspaceMember currentUserMembership = workspaceMemberPersistencePort
                .findByWorkspaceIdAndUserId(workspaceId, currentUserId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "You are not a member of this workspace"));

        // Owner cannot leave the workspace
        if (currentUserMembership.getRole() == WorkspaceRole.OWNER) {
            throw new IllegalArgumentException(
                    "Workspace owner cannot leave. Transfer ownership to someone else first.");
        }

        // Remove the member from workspace
        workspaceMemberPersistencePort.delete(currentUserMembership);
    }
}
