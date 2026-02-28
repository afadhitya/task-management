package com.afadhitya.taskmanagement.application.usecase.task;

import com.afadhitya.taskmanagement.application.dto.request.UpdateTaskRequest;
import com.afadhitya.taskmanagement.application.dto.response.TaskResponse;
import com.afadhitya.taskmanagement.application.mapper.TaskMapper;
import com.afadhitya.taskmanagement.application.port.in.task.UpdateTaskUseCase;
import com.afadhitya.taskmanagement.application.port.out.task.TaskPersistencePort;
import com.afadhitya.taskmanagement.application.service.AuditEventPublisher;
import com.afadhitya.taskmanagement.domain.entity.Task;
import com.afadhitya.taskmanagement.domain.enums.AuditAction;
import com.afadhitya.taskmanagement.domain.enums.AuditEntityType;
import com.afadhitya.taskmanagement.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateTaskUseCaseImpl implements UpdateTaskUseCase {

    private final TaskPersistencePort taskPersistencePort;
    private final TaskMapper taskMapper;
    private final AuditEventPublisher auditEventPublisher;

    @Override
    public TaskResponse updateTask(Long id, UpdateTaskRequest request) {
        Task task = taskPersistencePort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id));

        Map<String, Object> diff = new HashMap<>();

        if (request.title() != null && !request.title().equals(task.getTitle())) {
            diff.put("title", Map.of("old", task.getTitle(), "new", request.title()));
        }
        if (request.description() != null && !request.description().equals(task.getDescription())) {
            diff.put("description", Map.of("old", task.getDescription(), "new", request.description()));
        }
        if (request.status() != null && request.status() != task.getStatus()) {
            diff.put("status", Map.of("old", task.getStatus().name(), "new", request.status().name()));
        }
        if (request.priority() != null && request.priority() != task.getPriority()) {
            diff.put("priority", Map.of("old", task.getPriority().name(), "new", request.priority().name()));
        }
        if (request.dueDate() != null && !request.dueDate().equals(task.getDueDate())) {
            diff.put("dueDate", Map.of("old", task.getDueDate(), "new", request.dueDate()));
        }
        if (request.assigneeIds() != null && !request.assigneeIds().equals(task.getAssigneeIds())) {
            diff.put("assigneeIds", Map.of("old", task.getAssigneeIds(), "new", request.assigneeIds()));
        }

        taskMapper.updateEntityFromRequest(request, task);

        Task updatedTask = taskPersistencePort.save(task);

        if (!diff.isEmpty()) {
            AuditAction action = diff.containsKey("status") ? AuditAction.STATUS_CHANGE : AuditAction.UPDATE;
            auditEventPublisher.publish(
                    task.getProject().getWorkspace().getId(),
                    SecurityUtils.getCurrentUserId(),
                    AuditEntityType.TASK,
                    id,
                    action,
                    diff
            );
        }

        return taskMapper.toResponse(updatedTask);
    }
}
