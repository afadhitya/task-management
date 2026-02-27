package com.afadhitya.taskmanagement.application.port.in.workspace;

import com.afadhitya.taskmanagement.domain.enums.WorkspaceRole;

import java.util.Optional;

public interface WorkspacePermissionUseCase {

    boolean hasRole(Long workspaceId, Long userId, WorkspaceRole... roles);

    boolean hasAnyRole(Long workspaceId, Long userId, WorkspaceRole... roles);

    boolean isMember(Long workspaceId, Long userId);

    Optional<WorkspaceRole> getUserRole(Long workspaceId, Long userId);

    void validateAccess(Long workspaceId, Long userId, WorkspaceRole... requiredRoles);

    void validateIsMember(Long workspaceId, Long userId);
}
