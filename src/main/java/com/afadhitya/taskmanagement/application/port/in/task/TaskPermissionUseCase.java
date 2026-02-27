package com.afadhitya.taskmanagement.application.port.in.task;

public interface TaskPermissionUseCase {

    boolean canViewTask(Long taskId, Long userId);

    boolean canContributeToTask(Long taskId, Long userId);

    boolean canManageTask(Long taskId, Long userId);
}
