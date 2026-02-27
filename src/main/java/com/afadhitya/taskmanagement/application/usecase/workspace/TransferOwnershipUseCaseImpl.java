package com.afadhitya.taskmanagement.application.usecase.workspace;

import com.afadhitya.taskmanagement.application.dto.request.TransferOwnershipRequest;
import com.afadhitya.taskmanagement.application.dto.response.WorkspaceMemberResponse;
import com.afadhitya.taskmanagement.application.port.in.workspace.TransferOwnershipUseCase;
import com.afadhitya.taskmanagement.application.port.out.workspace.WorkspaceMemberPersistencePort;
import com.afadhitya.taskmanagement.application.port.out.workspace.WorkspacePersistencePort;
import com.afadhitya.taskmanagement.domain.entity.Workspace;
import com.afadhitya.taskmanagement.domain.entity.WorkspaceMember;
import com.afadhitya.taskmanagement.domain.enums.WorkspaceRole;
import com.afadhitya.taskmanagement.domain.exception.NotWorkspaceOwnerException;
import com.afadhitya.taskmanagement.domain.exception.WorkspaceAccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TransferOwnershipUseCaseImpl implements TransferOwnershipUseCase {

    private final WorkspacePersistencePort workspacePersistencePort;
    private final WorkspaceMemberPersistencePort workspaceMemberPersistencePort;

    @Override
    public WorkspaceMemberResponse transferOwnership(Long workspaceId, TransferOwnershipRequest request, Long currentUserId) {
        // Verify workspace exists
        Workspace workspace = workspacePersistencePort.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found with id: " + workspaceId));

        // Verify current user is the OWNER
        WorkspaceMember currentOwnerMembership = workspaceMemberPersistencePort
                .findByWorkspaceIdAndUserId(workspaceId, currentUserId)
                .orElseThrow(() -> new WorkspaceAccessDeniedException(
                        "You are not a member of this workspace"));

        if (currentOwnerMembership.getRole() != WorkspaceRole.OWNER) {
            throw new NotWorkspaceOwnerException(workspaceId, currentUserId);
        }

        // Verify new owner is already a member of the workspace
        WorkspaceMember newOwnerMembership = workspaceMemberPersistencePort
                .findByWorkspaceIdAndUserId(workspaceId, request.newOwnerId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "The new owner must already be a member of this workspace"));

        // Prevent transferring to self
        if (request.newOwnerId().equals(currentUserId)) {
            throw new IllegalArgumentException("You are already the owner of this workspace");
        }

        // Update current owner to ADMIN
        currentOwnerMembership.setRole(WorkspaceRole.ADMIN);
        workspaceMemberPersistencePort.save(currentOwnerMembership);

        // Update new owner to OWNER
        newOwnerMembership.setRole(WorkspaceRole.OWNER);
        WorkspaceMember updatedNewOwner = workspaceMemberPersistencePort.save(newOwnerMembership);

        // Update workspace owner reference
        workspace.setOwner(newOwnerMembership.getUser());
        workspacePersistencePort.save(workspace);

        return mapToResponse(updatedNewOwner);
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
