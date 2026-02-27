package com.afadhitya.taskmanagement.adapter.in.web;

import com.afadhitya.taskmanagement.application.dto.request.CreateProjectRequest;
import com.afadhitya.taskmanagement.application.dto.response.ProjectResponse;
import com.afadhitya.taskmanagement.application.port.in.project.CreateProjectUseCase;
import com.afadhitya.taskmanagement.application.port.in.project.GetProjectsByWorkspaceUseCase;
import com.afadhitya.taskmanagement.infrastructure.config.OpenApiConfig;
import com.afadhitya.taskmanagement.infrastructure.security.SecurityUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workspaces/{workspaceId}/projects")
@RequiredArgsConstructor
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class ProjectController {

    private final CreateProjectUseCase createProjectUseCase;
    private final GetProjectsByWorkspaceUseCase getProjectsByWorkspaceUseCase;

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @PathVariable Long workspaceId,
            @Valid @RequestBody CreateProjectRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        // Override workspaceId from path to ensure consistency
        CreateProjectRequest requestWithWorkspaceId = CreateProjectRequest.builder()
                .name(request.name())
                .description(request.description())
                .color(request.color())
                .workspaceId(workspaceId)
                .build();
        ProjectResponse response = createProjectUseCase.createProject(requestWithWorkspaceId, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getProjectsByWorkspace(@PathVariable Long workspaceId) {
        List<ProjectResponse> projects = getProjectsByWorkspaceUseCase.getProjectsByWorkspace(workspaceId);
        return ResponseEntity.ok(projects);
    }
}
