    package com.afadhitya.taskmanagement.application.usecase.workspace;

import com.afadhitya.taskmanagement.application.port.in.workspace.WorkspacePermissionUseCase;
import com.afadhitya.taskmanagement.application.port.out.workspace.WorkspaceMemberPersistencePort;
import com.afadhitya.taskmanagement.domain.enums.WorkspaceRole;
import com.afadhitya.taskmanagement.domain.exception.WorkspaceAccessDeniedException;
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
public class WorkspacePermissionUseCaseImpl implements WorkspacePermissionUseCase {

    private final WorkspaceMemberPersistencePort workspaceMemberPersistencePort;

    @Override
    public boolean hasRole(Long workspaceId, Long userId, WorkspaceRole... roles) {
        if (roles == null || roles.length == 0) {
            return isMember(workspaceId, userId);
        }
        Optional<WorkspaceRole> userRole = getUserRole(workspaceId, userId);
        if (userRole.isEmpty()) {
            return false;
        }
        Set<WorkspaceRole> requiredRoles = Arrays.stream(roles).collect(Collectors.toSet());
        return requiredRoles.contains(userRole.get());
    }

    @Override
    public boolean hasAnyRole(Long workspaceId, Long userId, WorkspaceRole... roles) {
        return hasRole(workspaceId, userId, roles);
    }

    @Override
    public boolean isMember(Long workspaceId, Long userId) {
        return workspaceMemberPersistencePort.existsByWorkspaceIdAndUserId(workspaceId, userId);
    }

    @Override
    public Optional<WorkspaceRole> getUserRole(Long workspaceId, Long userId) {
        return workspaceMemberPersistencePort.findRoleByWorkspaceIdAndUserId(workspaceId, userId);
    }

    @Override
    public void validateAccess(Long workspaceId, Long userId, WorkspaceRole... requiredRoles) {
        if (!hasRole(workspaceId, userId, requiredRoles)) {
            throw new WorkspaceAccessDeniedException(workspaceId, userId);
        }
    }

    @Override
    public void validateIsMember(Long workspaceId, Long userId) {
        if (!isMember(workspaceId, userId)) {
            throw new WorkspaceAccessDeniedException(workspaceId, userId);
        }
    }
}
