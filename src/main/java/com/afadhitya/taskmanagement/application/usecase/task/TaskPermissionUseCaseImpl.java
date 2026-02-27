package com.afadhitya.taskmanagement.application.usecase.task;

import com.afadhitya.taskmanagement.application.port.in.project.ProjectPermissionUseCase;
import com.afadhitya.taskmanagement.application.port.in.task.TaskPermissionUseCase;
import com.afadhitya.taskmanagement.application.port.out.task.TaskPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskPermissionUseCaseImpl implements TaskPermissionUseCase {

    private final TaskPersistencePort taskPersistencePort;
    private final ProjectPermissionUseCase projectPermissionUseCase;

    @Override
    public boolean canViewTask(Long taskId, Long userId) {
        Long projectId = getProjectIdByTaskId(taskId);
        return projectPermissionUseCase.canViewProject(projectId, userId);
    }

    @Override
    public boolean canContributeToTask(Long taskId, Long userId) {
        Long projectId = getProjectIdByTaskId(taskId);
        return projectPermissionUseCase.canContributeToProject(projectId, userId);
    }

    @Override
    public boolean canManageTask(Long taskId, Long userId) {
        Long projectId = getProjectIdByTaskId(taskId);
        return projectPermissionUseCase.canManageProject(projectId, userId);
    }

    private Long getProjectIdByTaskId(Long taskId) {
        Task task = taskPersistencePort.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + taskId));
        return task.getProject().getId();
    }
}
