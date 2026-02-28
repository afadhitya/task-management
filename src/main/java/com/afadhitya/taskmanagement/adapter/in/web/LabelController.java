package com.afadhitya.taskmanagement.adapter.in.web;

import com.afadhitya.taskmanagement.application.dto.request.CreateLabelRequest;
import com.afadhitya.taskmanagement.application.dto.request.UpdateLabelRequest;
import com.afadhitya.taskmanagement.application.dto.response.LabelResponse;
import com.afadhitya.taskmanagement.application.port.in.label.CreateLabelUseCase;
import com.afadhitya.taskmanagement.application.port.in.label.GetLabelsByProjectUseCase;
import com.afadhitya.taskmanagement.application.port.in.label.UpdateLabelUseCase;
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
public class LabelController {

    private final CreateLabelUseCase createLabelUseCase;
    private final GetLabelsByProjectUseCase getLabelsByProjectUseCase;
    private final UpdateLabelUseCase updateLabelUseCase;

    @PreAuthorize("@workspaceSecurity.isWorkspaceMember(#workspaceId)")
    @PostMapping("/workspaces/{workspaceId}/labels")
    public ResponseEntity<LabelResponse> createLabel(
            @PathVariable Long workspaceId,
            @Valid @RequestBody CreateLabelRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        LabelResponse response = createLabelUseCase.createLabel(workspaceId, request, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("@projectSecurity.canViewProject(#projectId)")
    @GetMapping("/projects/{projectId}/labels")
    public ResponseEntity<List<LabelResponse>> getLabelsByProject(@PathVariable Long projectId) {
        List<LabelResponse> labels = getLabelsByProjectUseCase.getLabelsByProject(projectId);
        return ResponseEntity.ok(labels);
    }

    @PatchMapping("/labels/{id}")
    public ResponseEntity<LabelResponse> updateLabel(
            @PathVariable Long id,
            @Valid @RequestBody UpdateLabelRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        LabelResponse response = updateLabelUseCase.updateLabel(id, request, currentUserId);
        return ResponseEntity.ok(response);
    }
}
