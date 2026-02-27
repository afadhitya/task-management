package com.afadhitya.taskmanagement.adapter.in.web;

import com.afadhitya.taskmanagement.application.dto.request.AddProjectMemberRequest;
import com.afadhitya.taskmanagement.application.dto.request.CreateProjectRequest;
import com.afadhitya.taskmanagement.application.dto.request.UpdateProjectRequest;
import com.afadhitya.taskmanagement.application.dto.response.ProjectMemberResponse;
import com.afadhitya.taskmanagement.application.dto.response.ProjectResponse;
import com.afadhitya.taskmanagement.application.port.in.project.AddProjectMemberUseCase;
import com.afadhitya.taskmanagement.application.port.in.project.CreateProjectUseCase;
import com.afadhitya.taskmanagement.application.port.in.project.DeleteProjectUseCase;
import com.afadhitya.taskmanagement.application.port.in.project.GetProjectByIdUseCase;
import com.afadhitya.taskmanagement.application.port.in.project.GetProjectsByWorkspaceUseCase;
import com.afadhitya.taskmanagement.application.port.in.project.RemoveProjectMemberUseCase;
import com.afadhitya.taskmanagement.application.port.in.project.UpdateProjectUseCase;
import com.afadhitya.taskmanagement.infrastructure.config.OpenApiConfig;
import com.afadhitya.taskmanagement.infrastructure.security.SecurityUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class ProjectController {

    private final CreateProjectUseCase createProjectUseCase;
    private final GetProjectsByWorkspaceUseCase getProjectsByWorkspaceUseCase;
    private final GetProjectByIdUseCase getProjectByIdUseCase;
    private final UpdateProjectUseCase updateProjectUseCase;
    private final DeleteProjectUseCase deleteProjectUseCase;
    private final AddProjectMemberUseCase addProjectMemberUseCase;
    private final RemoveProjectMemberUseCase removeProjectMemberUseCase;

    @PreAuthorize("@workspaceSecurity.isWorkspaceMember(#workspaceId)")
    @PostMapping("/workspaces/{workspaceId}/projects")
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

    @PreAuthorize("@workspaceSecurity.isWorkspaceMember(#workspaceId)")
    @GetMapping("/workspaces/{workspaceId}/projects")
    public ResponseEntity<List<ProjectResponse>> getProjectsByWorkspace(@PathVariable Long workspaceId) {
        List<ProjectResponse> projects = getProjectsByWorkspaceUseCase.getProjectsByWorkspace(workspaceId);
        return ResponseEntity.ok(projects);
    }

    @PreAuthorize("@projectSecurity.canViewProject(#id)")
    @GetMapping("/projects/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        ProjectResponse project = getProjectByIdUseCase.getProjectById(id);
        return ResponseEntity.ok(project);
    }

    @PreAuthorize("@projectSecurity.canManageProject(#id)")
    @PatchMapping("/projects/{id}")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProjectRequest request) {
        ProjectResponse response = updateProjectUseCase.updateProject(id, request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("@projectSecurity.canManageProject(#id)")
    @DeleteMapping("/projects/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        deleteProjectUseCase.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("@projectSecurity.canManageProject(#id)")
    @PostMapping("/projects/{id}/members")
    public ResponseEntity<ProjectMemberResponse> addMember(
            @PathVariable Long id,
            @Valid @RequestBody AddProjectMemberRequest request) {
        ProjectMemberResponse response = addProjectMemberUseCase.addMember(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("@projectSecurity.canManageProject(#id)")
    @DeleteMapping("/projects/{id}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long id,
            @PathVariable Long userId) {
        removeProjectMemberUseCase.removeMember(id, userId);
        return ResponseEntity.noContent().build();
    }
}
