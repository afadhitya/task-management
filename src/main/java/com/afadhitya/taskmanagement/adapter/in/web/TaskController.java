package com.afadhitya.taskmanagement.adapter.in.web;

import com.afadhitya.taskmanagement.application.dto.request.CreateTaskRequest;
import com.afadhitya.taskmanagement.application.dto.response.TaskResponse;
import com.afadhitya.taskmanagement.application.port.in.task.CreateTaskUseCase;
import com.afadhitya.taskmanagement.infrastructure.config.OpenApiConfig;
import com.afadhitya.taskmanagement.infrastructure.security.SecurityUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class TaskController {

    private final CreateTaskUseCase createTaskUseCase;

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
}
