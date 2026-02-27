package com.afadhitya.taskmanagement.application.usecase.project;

import com.afadhitya.taskmanagement.application.dto.request.UpdateProjectMemberRoleRequest;
import com.afadhitya.taskmanagement.application.dto.response.ProjectMemberResponse;
import com.afadhitya.taskmanagement.application.mapper.ProjectMemberMapper;
import com.afadhitya.taskmanagement.application.port.in.project.UpdateProjectMemberRoleUseCase;
import com.afadhitya.taskmanagement.application.port.in.workspace.WorkspacePermissionUseCase;
import com.afadhitya.taskmanagement.application.port.out.project.ProjectMemberPersistencePort;
import com.afadhitya.taskmanagement.application.port.out.project.ProjectPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.Project;
import com.afadhitya.taskmanagement.domain.entity.ProjectMember;
import com.afadhitya.taskmanagement.domain.enums.ProjectPermission;
import com.afadhitya.taskmanagement.domain.enums.WorkspaceRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateProjectMemberRoleUseCaseImpl implements UpdateProjectMemberRoleUseCase {

    private final ProjectMemberPersistencePort projectMemberPersistencePort;
    private final ProjectPersistencePort projectPersistencePort;
    private final WorkspacePermissionUseCase workspacePermissionUseCase;
    private final ProjectMemberMapper projectMemberMapper;

    @Override
    public ProjectMemberResponse updateMemberRole(Long projectId, Long targetUserId, UpdateProjectMemberRoleRequest request, Long currentUserId) {
        Project project = projectPersistencePort.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + projectId));

        ProjectMember targetMember = projectMemberPersistencePort.findByProjectIdAndUserId(projectId, targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("User is not a member of this project"));

        // Check if current user has permission to change roles
        boolean isWorkspaceOwnerOrAdmin = isWorkspaceOwnerOrAdmin(project.getWorkspace().getId(), currentUserId);
        boolean isProjectManager = isProjectManager(projectId, currentUserId);

        if (!isWorkspaceOwnerOrAdmin && !isProjectManager) {
            throw new IllegalStateException("You don't have permission to change member roles");
        }

        // If current user is PROJECT MANAGER (not workspace owner/admin), additional restrictions apply
        if (!isWorkspaceOwnerOrAdmin && isProjectManager) {
            // Project Manager cannot change another Manager's role
            if (targetMember.getPermission() == ProjectPermission.MANAGER) {
                throw new IllegalStateException("Project Managers cannot change another Manager's role. Contact workspace admin.");
            }
        }

        // Check for last manager safeguard
        if (targetMember.getPermission() == ProjectPermission.MANAGER && request.permission() != ProjectPermission.MANAGER) {
            long managerCount = projectMemberPersistencePort.countByProjectIdAndPermission(projectId, ProjectPermission.MANAGER);
            if (managerCount <= 1) {
                throw new IllegalStateException("Cannot demote the last manager. At least one manager must remain in the project.");
            }
        }

        targetMember.setPermission(request.permission());
        ProjectMember updatedMember = projectMemberPersistencePort.save(targetMember);

        return projectMemberMapper.toResponse(updatedMember);
    }

    private boolean isWorkspaceOwnerOrAdmin(Long workspaceId, Long userId) {
        return workspacePermissionUseCase.hasRole(workspaceId, userId, WorkspaceRole.OWNER, WorkspaceRole.ADMIN);
    }

    private boolean isProjectManager(Long projectId, Long userId) {
        return projectMemberPersistencePort.findByProjectIdAndUserId(projectId, userId)
                .map(member -> member.getPermission() == ProjectPermission.MANAGER)
                .orElse(false);
    }
}
