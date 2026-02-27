package com.afadhitya.taskmanagement.application.usecase.project;

import com.afadhitya.taskmanagement.application.port.in.project.ProjectPermissionUseCase;
import com.afadhitya.taskmanagement.application.port.in.workspace.WorkspacePermissionUseCase;
import com.afadhitya.taskmanagement.application.port.out.project.ProjectMemberPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.ProjectMember;
import com.afadhitya.taskmanagement.domain.enums.ProjectPermission;
import com.afadhitya.taskmanagement.domain.enums.WorkspaceRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectPermissionUseCaseImpl implements ProjectPermissionUseCase {

    private final ProjectMemberPersistencePort projectMemberPersistencePort;
    private final WorkspacePermissionUseCase workspacePermissionUseCase;

    @Override
    public boolean hasPermission(Long projectId, Long userId, ProjectPermission... permissions) {
        if (permissions == null || permissions.length == 0) {
            return false;
        }

        Set<ProjectPermission> requiredPermissions = Arrays.stream(permissions).collect(Collectors.toSet());
        return requiredPermissions.contains(getEffectivePermission(projectId, userId, null));
    }

    @Override
    public boolean canViewProject(Long projectId, Long userId) {
        return hasPermission(projectId, userId, ProjectPermission.VIEW, ProjectPermission.CONTRIBUTOR, ProjectPermission.MANAGER);
    }

    @Override
    public boolean canContributeToProject(Long projectId, Long userId) {
        return hasPermission(projectId, userId, ProjectPermission.CONTRIBUTOR, ProjectPermission.MANAGER);
    }

    @Override
    public boolean canManageProject(Long projectId, Long userId) {
        return hasPermission(projectId, userId, ProjectPermission.MANAGER);
    }

    @Override
    public ProjectPermission getEffectivePermission(Long projectId, Long userId, Long workspaceId) {
        // Get user's workspace role if workspaceId provided, otherwise try to determine from project
        Optional<WorkspaceRole> workspaceRoleOpt = workspacePermissionUseCase.getUserRole(workspaceId, userId);

        // If not a workspace member at all, check if they have explicit project membership (GUEST scenario)
        if (workspaceRoleOpt.isEmpty()) {
            Optional<ProjectMember> projectMember = projectMemberPersistencePort.findByProjectIdAndUserId(projectId, userId);
            return projectMember.map(ProjectMember::getPermission).orElse(null);
        }

        WorkspaceRole workspaceRole = workspaceRoleOpt.get();

        // Workspace OWNER always has MANAGER permission on all projects
        if (workspaceRole == WorkspaceRole.OWNER) {
            return ProjectPermission.MANAGER;
        }

        // Check for explicit project membership
        Optional<ProjectMember> projectMemberOpt = projectMemberPersistencePort.findByProjectIdAndUserId(projectId, userId);

        if (projectMemberOpt.isPresent()) {
            return projectMemberOpt.get().getPermission();
        }

        // No explicit project membership - use defaults based on workspace role
        return switch (workspaceRole) {
            case ADMIN -> ProjectPermission.MANAGER;
            case MEMBER -> ProjectPermission.CONTRIBUTOR;
            case GUEST -> null; // GUEST without explicit project membership has no access
            default -> null;
        };
    }
}
