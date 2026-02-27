package com.afadhitya.taskmanagement.adapter.in.web;

import com.afadhitya.taskmanagement.application.dto.request.BulkUpdateTasksRequest;
import com.afadhitya.taskmanagement.application.dto.request.CreateSubtaskRequest;
import com.afadhitya.taskmanagement.application.dto.request.CreateTaskRequest;
import com.afadhitya.taskmanagement.application.dto.request.TaskFilterRequest;
import com.afadhitya.taskmanagement.application.dto.request.UpdateTaskRequest;
import com.afadhitya.taskmanagement.application.dto.response.BulkJobResponse;
import com.afadhitya.taskmanagement.application.dto.response.PagedResponse;
import com.afadhitya.taskmanagement.application.dto.response.TaskResponse;
import com.afadhitya.taskmanagement.application.port.in.bulkjob.SubmitBulkJobUseCase;
import com.afadhitya.taskmanagement.application.port.in.task.CreateSubtaskUseCase;
import com.afadhitya.taskmanagement.application.port.in.task.CreateTaskUseCase;
import com.afadhitya.taskmanagement.application.port.in.task.DeleteTaskUseCase;
import com.afadhitya.taskmanagement.application.port.in.task.GetTaskByIdUseCase;
import com.afadhitya.taskmanagement.application.port.in.task.GetTasksByProjectUseCase;
import com.afadhitya.taskmanagement.application.port.in.task.UpdateTaskUseCase;
import com.afadhitya.taskmanagement.domain.enums.TaskPriority;
import com.afadhitya.taskmanagement.domain.enums.TaskStatus;
import com.afadhitya.taskmanagement.infrastructure.config.OpenApiConfig;
import com.afadhitya.taskmanagement.infrastructure.security.SecurityUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class TaskController {

    private final CreateTaskUseCase createTaskUseCase;
    private final GetTasksByProjectUseCase getTasksByProjectUseCase;
    private final GetTaskByIdUseCase getTaskByIdUseCase;
    private final UpdateTaskUseCase updateTaskUseCase;
    private final DeleteTaskUseCase deleteTaskUseCase;
    private final CreateSubtaskUseCase createSubtaskUseCase;
    private final SubmitBulkJobUseCase submitBulkJobUseCase;

    @PreAuthorize("@projectSecurity.canContributeToProject(#projectId)")
    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable Long projectId,
            @Valid @RequestBody CreateTaskRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        // Override projectId from path to ensure consistency
        CreateTaskRequest requestWithProjectId = request.withProjectId(projectId);
        TaskResponse response = createTaskUseCase.createTask(requestWithProjectId, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("@projectSecurity.canViewProject(#projectId)")
    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<PagedResponse<TaskResponse>> getTasksByProject(
            @PathVariable Long projectId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority,
            @RequestParam(required = false) Set<Long> assigneeIds,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDateTo,
            @RequestParam(required = false) Long parentTaskId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {
        
        TaskFilterRequest filter = TaskFilterRequest.builder()
                .status(status)
                .priority(priority)
                .assigneeIds(assigneeIds)
                .dueDateFrom(dueDateFrom)
                .dueDateTo(dueDateTo)
                .parentTaskId(parentTaskId)
                .search(search)
                .build();
        
        PagedResponse<TaskResponse> response = getTasksByProjectUseCase.getTasksByProject(
                projectId, filter, page, size, sortBy, sortDirection);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("@taskSecurity.canViewTask(#id)")
    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        TaskResponse task = getTaskByIdUseCase.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @PreAuthorize("@taskSecurity.canContributeToTask(#id)")
    @PatchMapping("/tasks/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequest request) {
        TaskResponse response = updateTaskUseCase.updateTask(id, request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("@taskSecurity.canManageTask(#id)")
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        deleteTaskUseCase.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("@taskSecurity.canContributeToTask(#id)")
    @PostMapping("/tasks/{id}/subtasks")
    public ResponseEntity<TaskResponse> createSubtask(
            @PathVariable Long id,
            @Valid @RequestBody CreateSubtaskRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        TaskResponse response = createSubtaskUseCase.createSubtask(id, request, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("@projectSecurity.canContributeToProject(#projectId)")
    @PatchMapping("/projects/{projectId}/tasks/bulk")
    public ResponseEntity<BulkJobResponse> bulkUpdateTasks(
            @PathVariable Long projectId,
            @Valid @RequestBody BulkUpdateTasksRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        BulkJobResponse response = submitBulkJobUseCase.submitBulkUpdateTasks(projectId, request, currentUserId);
        return ResponseEntity.ok(response);
    }
}
