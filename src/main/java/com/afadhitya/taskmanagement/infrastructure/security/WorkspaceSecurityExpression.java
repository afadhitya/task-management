package com.afadhitya.taskmanagement.infrastructure.security;

import com.afadhitya.taskmanagement.application.port.in.workspace.WorkspacePermissionUseCase;
import com.afadhitya.taskmanagement.domain.enums.WorkspaceRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component("workspaceSecurity")
@RequiredArgsConstructor
public class WorkspaceSecurityExpression {

    private final WorkspacePermissionUseCase workspacePermissionUseCase;

    public boolean hasWorkspaceRole(Long workspaceId, String... roles) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        WorkspaceRole[] workspaceRoles = Arrays.stream(roles)
                .map(WorkspaceRole::valueOf)
                .toArray(WorkspaceRole[]::new);
        return workspacePermissionUseCase.hasRole(workspaceId, currentUserId, workspaceRoles);
    }

    public boolean isWorkspaceMember(Long workspaceId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return workspacePermissionUseCase.isMember(workspaceId, currentUserId);
    }

    public boolean isWorkspaceOwner(Long workspaceId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return workspacePermissionUseCase.hasRole(workspaceId, currentUserId, WorkspaceRole.OWNER);
    }

    public boolean hasWorkspaceAdminOrHigher(Long workspaceId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return workspacePermissionUseCase.hasRole(workspaceId, currentUserId, WorkspaceRole.OWNER, WorkspaceRole.ADMIN);
    }
}
