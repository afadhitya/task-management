package com.afadhitya.taskmanagement.infrastructure.security;

import com.afadhitya.taskmanagement.application.port.in.project.ProjectPermissionUseCase;
import com.afadhitya.taskmanagement.application.port.in.workspace.WorkspacePermissionUseCase;
import com.afadhitya.taskmanagement.application.port.out.label.LabelPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.Label;
import com.afadhitya.taskmanagement.domain.enums.WorkspaceRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("labelSecurity")
@RequiredArgsConstructor
public class LabelSecurityExpression {

    private final LabelPersistencePort labelPersistencePort;
    private final WorkspacePermissionUseCase workspacePermissionUseCase;
    private final ProjectPermissionUseCase projectPermissionUseCase;

    public boolean canCreateLabel(Long workspaceId, Long projectId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        if (projectId == null) {
            // Global label - only OWNER/ADMIN can update
            return workspacePermissionUseCase.hasRole(workspaceId, currentUserId, WorkspaceRole.OWNER, WorkspaceRole.ADMIN);
        } else {
            // Project label - OWNER/ADMIN or Project MANAGER can update
            boolean isWorkspaceAdmin = workspacePermissionUseCase.hasRole(workspaceId, currentUserId, WorkspaceRole.OWNER, WorkspaceRole.ADMIN);
            boolean isProjectManager = projectPermissionUseCase.canManageProject(projectId, currentUserId);

            return isWorkspaceAdmin || isProjectManager;
        }
    }

    public boolean canUpdateLabel(Long labelId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        Label label = labelPersistencePort.findById(labelId)
                .orElseThrow(() -> new IllegalArgumentException("Label not found with id: " + labelId));

        Long workspaceId = label.getWorkspace().getId();

        if (label.isGlobal()) {
            // Global label - only OWNER/ADMIN can update
            return workspacePermissionUseCase.hasRole(workspaceId, currentUserId, WorkspaceRole.OWNER, WorkspaceRole.ADMIN);
        } else {
            // Project label - OWNER/ADMIN or Project MANAGER can update
            Long projectId = label.getProject().getId();
            boolean isWorkspaceAdmin = workspacePermissionUseCase.hasRole(workspaceId, currentUserId, WorkspaceRole.OWNER, WorkspaceRole.ADMIN);
            boolean isProjectManager = projectPermissionUseCase.canManageProject(projectId, currentUserId);

            return isWorkspaceAdmin || isProjectManager;
        }
    }

    public boolean canDeleteLabel(Long labelId) {
        // Same permission as update
        return canUpdateLabel(labelId);
    }
}
