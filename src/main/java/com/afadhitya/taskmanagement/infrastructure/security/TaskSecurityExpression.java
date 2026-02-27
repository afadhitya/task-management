package com.afadhitya.taskmanagement.infrastructure.security;

import com.afadhitya.taskmanagement.application.port.in.task.TaskPermissionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("taskSecurity")
@RequiredArgsConstructor
public class TaskSecurityExpression {

    private final TaskPermissionUseCase taskPermissionUseCase;

    public boolean canViewTask(Long taskId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return taskPermissionUseCase.canViewTask(taskId, currentUserId);
    }

    public boolean canContributeToTask(Long taskId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return taskPermissionUseCase.canContributeToTask(taskId, currentUserId);
    }

    public boolean canManageTask(Long taskId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return taskPermissionUseCase.canManageTask(taskId, currentUserId);
    }
}
