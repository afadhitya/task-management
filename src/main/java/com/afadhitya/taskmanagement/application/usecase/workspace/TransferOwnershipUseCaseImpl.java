package com.afadhitya.taskmanagement.application.usecase.workspace;

import com.afadhitya.taskmanagement.application.dto.request.TransferOwnershipRequest;
import com.afadhitya.taskmanagement.application.dto.response.WorkspaceMemberResponse;
import com.afadhitya.taskmanagement.application.port.in.workspace.TransferOwnershipUseCase;
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
public class TransferOwnershipUseCaseImpl implements TransferOwnershipUseCase {

    private final WorkspacePersistencePort workspacePersistencePort;
    private final WorkspaceMemberPersistencePort workspaceMemberPersistencePort;

    @Override
    public WorkspaceMemberResponse transferOwnership(Long workspaceId, TransferOwnershipRequest request, Long currentUserId) {
        Workspace workspace = workspacePersistencePort.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found with id: " + workspaceId));

        WorkspaceMember currentOwnerMembership = workspaceMemberPersistencePort
                .findByWorkspaceIdAndUserId(workspaceId, currentUserId)
                .orElseThrow(() -> new IllegalStateException("Current user membership not found"));

        WorkspaceMember newOwnerMembership = workspaceMemberPersistencePort
                .findByWorkspaceIdAndUserId(workspaceId, request.newOwnerId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "The new owner must already be a member of this workspace"));

        if (request.newOwnerId().equals(currentUserId)) {
            throw new IllegalArgumentException("You are already the owner of this workspace");
        }

        if (newOwnerMembership.getRole() == WorkspaceRole.GUEST) {
            throw new IllegalArgumentException(
                    "Cannot transfer ownership to a user with GUEST role");
        }

        currentOwnerMembership.setRole(WorkspaceRole.ADMIN);
        workspaceMemberPersistencePort.save(currentOwnerMembership);

        newOwnerMembership.setRole(WorkspaceRole.OWNER);
        WorkspaceMember updatedNewOwner = workspaceMemberPersistencePort.save(newOwnerMembership);

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
