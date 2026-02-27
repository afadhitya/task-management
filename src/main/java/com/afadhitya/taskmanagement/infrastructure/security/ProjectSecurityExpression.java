package com.afadhitya.taskmanagement.infrastructure.security;

import com.afadhitya.taskmanagement.application.port.in.project.ProjectPermissionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("projectSecurity")
@RequiredArgsConstructor
public class ProjectSecurityExpression {

    private final ProjectPermissionUseCase projectPermissionUseCase;

    public boolean canViewProject(Long projectId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return projectPermissionUseCase.canViewProject(projectId, currentUserId);
    }

    public boolean canContributeToProject(Long projectId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return projectPermissionUseCase.canContributeToProject(projectId, currentUserId);
    }

    public boolean canManageProject(Long projectId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return projectPermissionUseCase.canManageProject(projectId, currentUserId);
    }

    public boolean isProjectMember(Long projectId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return projectPermissionUseCase.canViewProject(projectId, currentUserId);
    }
}
