package com.afadhitya.taskmanagement.application.usecase.task;

import com.afadhitya.taskmanagement.application.port.in.task.DeleteTaskUseCase;
import com.afadhitya.taskmanagement.application.port.out.task.TaskPersistencePort;
import com.afadhitya.taskmanagement.application.service.AuditEventPublisher;
import com.afadhitya.taskmanagement.domain.entity.Task;
import com.afadhitya.taskmanagement.domain.enums.AuditEntityType;
import com.afadhitya.taskmanagement.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteTaskUseCaseImpl implements DeleteTaskUseCase {

    private final TaskPersistencePort taskPersistencePort;
    private final AuditEventPublisher auditEventPublisher;

    @Override
    public void deleteTask(Long id) {
        Task task = taskPersistencePort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id));

        Long workspaceId = task.getProject().getWorkspace().getId();

        auditEventPublisher.publishDelete(
                workspaceId,
                SecurityUtils.getCurrentUserId(),
                AuditEntityType.TASK,
                id,
                Map.of(
                        "title", task.getTitle(),
                        "status", task.getStatus().name(),
                        "projectId", task.getProject().getId()
                )
        );

        taskPersistencePort.deleteById(id);
    }
}
