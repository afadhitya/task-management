package com.afadhitya.taskmanagement.application.port.in.project;

import com.afadhitya.taskmanagement.domain.enums.ProjectPermission;

public interface ProjectPermissionUseCase {

    boolean hasPermission(Long projectId, Long userId, ProjectPermission... permissions);

    boolean canViewProject(Long projectId, Long userId);

    boolean canContributeToProject(Long projectId, Long userId);

    boolean canManageProject(Long projectId, Long userId);

    ProjectPermission getEffectivePermission(Long projectId, Long userId, Long workspaceId);
}
