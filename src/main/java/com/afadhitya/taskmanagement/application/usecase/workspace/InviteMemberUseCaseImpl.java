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
import com.afadhitya.taskmanagement.domain.exception.WorkspaceAccessDeniedException;
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
        // Verify workspace exists
        Workspace workspace = workspacePersistencePort.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found with id: " + workspaceId));

        // Check if current user has ADMIN or OWNER role
        WorkspaceMember currentUserMembership = workspaceMemberPersistencePort
                .findByWorkspaceIdAndUserId(workspaceId, currentUserId)
                .orElseThrow(() -> new WorkspaceAccessDeniedException(
                        "You are not a member of this workspace"));

        if (currentUserMembership.getRole() != WorkspaceRole.ADMIN &&
            currentUserMembership.getRole() != WorkspaceRole.OWNER) {
            throw new WorkspaceAccessDeniedException(
                    "Only admins and owners can invite members to the workspace");
        }

        // Find the user to invite by email
        User userToInvite = userPersistencePort.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found with email: " + request.email()));

        // Check if user is already a member
        if (workspaceMemberPersistencePort.existsByWorkspaceIdAndUserId(workspaceId, userToInvite.getId())) {
            throw new IllegalArgumentException(
                    "User with email " + request.email() + " is already a member of this workspace");
        }

        // Validate that the role is not OWNER (only one owner should exist)
        if (request.role() == WorkspaceRole.OWNER) {
            throw new IllegalArgumentException(
                    "Cannot invite a member with OWNER role. Transfer ownership instead.");
        }

        // Get current user for invitedBy
        User currentUser = userPersistencePort.findById(currentUserId)
                .orElseThrow(() -> new IllegalStateException("Current user not found"));

        // Create the workspace member
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
