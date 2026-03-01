package com.afadhitya.taskmanagement.application.usecase.audit;

import com.afadhitya.taskmanagement.application.dto.request.CreateSubtaskRequest;
import com.afadhitya.taskmanagement.application.dto.request.CreateTaskRequest;
import com.afadhitya.taskmanagement.application.dto.request.UpdateTaskRequest;
import com.afadhitya.taskmanagement.application.dto.response.TaskResponse;
import com.afadhitya.taskmanagement.application.port.in.task.CreateSubtaskUseCase;
import com.afadhitya.taskmanagement.application.port.in.task.CreateTaskUseCase;
import com.afadhitya.taskmanagement.application.port.in.task.DeleteTaskUseCase;
import com.afadhitya.taskmanagement.application.port.in.task.UpdateTaskUseCase;
import com.afadhitya.taskmanagement.application.port.out.task.TaskPersistencePort;
import com.afadhitya.taskmanagement.application.service.AuditLogService;
import com.afadhitya.taskmanagement.application.usecase.feature.AuditFeatureInterceptor;
import com.afadhitya.taskmanagement.application.usecase.task.CreateSubtaskUseCaseImpl;
import com.afadhitya.taskmanagement.application.usecase.task.CreateTaskUseCaseImpl;
import com.afadhitya.taskmanagement.domain.entity.Task;
import com.afadhitya.taskmanagement.domain.enums.AuditAction;
import com.afadhitya.taskmanagement.domain.enums.AuditEntityType;
import com.afadhitya.taskmanagement.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Primary
@RequiredArgsConstructor
public class AuditedTaskUseCases {

    private final TaskPersistencePort taskPersistencePort;
    private final AuditLogService auditLogService;
    private final AuditFeatureInterceptor auditInterceptor;

    @Service
    @Primary
    @RequiredArgsConstructor
    public class CreateTask implements CreateTaskUseCase {

        private final CreateTaskUseCaseImpl delegate;

        @Override
        @Transactional
        public TaskResponse createTask(CreateTaskRequest request, Long createdByUserId) {
            TaskResponse response = delegate.createTask(request, createdByUserId);

            Task task = taskPersistencePort.findById(response.id()).orElseThrow();
            Long workspaceId = task.getProject().getWorkspace().getId();

            if (!auditInterceptor.shouldAudit(workspaceId)) {
                return response;
            }

            Map<String, Object> newValues = new HashMap<>();
            newValues.put("title", response.title());
            newValues.put("status", response.status().name());
            newValues.put("priority", response.priority().name());
            newValues.put("projectId", request.projectId());

            auditInterceptor.auditCreate(
                    workspaceId,
                    createdByUserId,
                    AuditEntityType.TASK,
                    response.id(),
                    newValues
            );

            return response;
        }
    }

    @Service
    @Primary
    @RequiredArgsConstructor
    public class CreateSubtask implements CreateSubtaskUseCase {

        private final CreateSubtaskUseCaseImpl delegate;

        @Override
        @Transactional
        public TaskResponse createSubtask(Long parentTaskId, CreateSubtaskRequest request, Long createdByUserId) {
            TaskResponse response = delegate.createSubtask(parentTaskId, request, createdByUserId);

            Task task = taskPersistencePort.findById(response.id()).orElseThrow();
            Long workspaceId = task.getProject().getWorkspace().getId();

            if (!auditInterceptor.shouldAudit(workspaceId)) {
                return response;
            }

            Map<String, Object> newValues = new HashMap<>();
            newValues.put("title", response.title());
            newValues.put("status", response.status().name());
            newValues.put("priority", response.priority().name());
            newValues.put("parentTaskId", parentTaskId);

            auditInterceptor.auditCreate(
                    workspaceId,
                    createdByUserId,
                    AuditEntityType.TASK,
                    response.id(),
                    newValues
            );

            return response;
        }
    }

    @Service
    @Primary
    @RequiredArgsConstructor
    public class UpdateTask implements UpdateTaskUseCase {

        private final com.afadhitya.taskmanagement.application.usecase.task.UpdateTaskUseCaseImpl delegate;

        @Override
        @Transactional
        public TaskResponse updateTask(Long id, UpdateTaskRequest request) {
            Task task = taskPersistencePort.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id));

            Long workspaceId = task.getProject().getWorkspace().getId();
            boolean shouldAudit = auditInterceptor.shouldAudit(workspaceId);

            Map<String, Object> diff = new HashMap<>();
            if (shouldAudit) {
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
            }

            TaskResponse response = delegate.updateTask(id, request);

            if (shouldAudit && !diff.isEmpty()) {
                auditInterceptor.auditUpdate(
                        workspaceId,
                        SecurityUtils.getCurrentUserId(),
                        AuditEntityType.TASK,
                        id,
                        diff
                );
            }

            return response;
        }
    }

    @Service
    @Primary
    @RequiredArgsConstructor
    public class DeleteTask implements DeleteTaskUseCase {

        private final com.afadhitya.taskmanagement.application.usecase.task.DeleteTaskUseCaseImpl delegate;

        @Override
        @Transactional
        public void deleteTask(Long id) {
            Task task = taskPersistencePort.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id));

            Long workspaceId = task.getProject().getWorkspace().getId();

            if (auditInterceptor.shouldAudit(workspaceId)) {
                auditInterceptor.auditDelete(
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
            }

            delegate.deleteTask(id);
        }
    }
}
