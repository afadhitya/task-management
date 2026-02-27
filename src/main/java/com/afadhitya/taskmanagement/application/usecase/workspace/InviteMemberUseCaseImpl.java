package com.afadhitya.taskmanagement.application.usecase.workspace;

import com.afadhitya.taskmanagement.application.dto.request.InviteMemberRequest;
import com.afadhitya.taskmanagement.application.dto.response.WorkspaceMemberResponse;
import com.afadhitya.taskmanagement.application.port.in.workspace.InviteMemberUseCase;
import com.afadhitya.taskmanagement.application.port.out.user.UserPersistencePort;
import com.afadhitya.taskmanagement.application.port.out.workspace.WorkspaceMemberPersistencePort;
import com.afadhitya.taskmanagement.application.port.out.workspace.WorkspacePersistencePort;
import com.afadhitya.taskmanagement.domain.entity.User;
import com.afadhitya.taskmanagement.domain.entity.Workspace;
import com.afadhitya.taskmanagement.domain.entity.WorkspaceMember;
import com.afadhitya.taskmanagement.domain.enums.WorkspaceRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InviteMemberUseCaseImpl implements InviteMemberUseCase {

    private final WorkspacePersistencePort workspacePersistencePort;
    private final WorkspaceMemberPersistencePort workspaceMemberPersistencePort;
    private final UserPersistencePort userPersistencePort;

    @Override
    public WorkspaceMemberResponse inviteMember(Long workspaceId, InviteMemberRequest request, Long currentUserId) {
        Workspace workspace = workspacePersistencePort.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found with id: " + workspaceId));

        User userToInvite = userPersistencePort.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found with email: " + request.email()));

        if (workspaceMemberPersistencePort.existsByWorkspaceIdAndUserId(workspaceId, userToInvite.getId())) {
            throw new IllegalArgumentException(
                    "User with email " + request.email() + " is already a member of this workspace");
        }

        if (request.role() == WorkspaceRole.OWNER) {
            throw new IllegalArgumentException(
                    "Cannot invite a member with OWNER role. Transfer ownership instead.");
        }

        User currentUser = userPersistencePort.findById(currentUserId)
                .orElseThrow(() -> new IllegalStateException("Current user not found"));

        WorkspaceMember newMember = WorkspaceMember.builder()
                .workspace(workspace)
                .user(userToInvite)
                .role(request.role())
                .invitedBy(currentUser)
                .build();

        WorkspaceMember savedMember = workspaceMemberPersistencePort.save(newMember);

        return mapToResponse(savedMember);
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
