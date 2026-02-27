package com.afadhitya.taskmanagement.adapter.in.web;

import com.afadhitya.taskmanagement.application.dto.CreateWorkspaceRequest;
import com.afadhitya.taskmanagement.application.dto.UpdateWorkspaceRequest;
import com.afadhitya.taskmanagement.application.dto.WorkspaceResponse;
import com.afadhitya.taskmanagement.application.port.in.workspace.CreateWorkspaceUseCase;
import com.afadhitya.taskmanagement.application.port.in.workspace.DeleteWorkspaceByIdUseCase;
import com.afadhitya.taskmanagement.application.port.in.workspace.GetWorkspaceByIdUseCase;
import com.afadhitya.taskmanagement.application.port.in.workspace.UpdateWorkspaceUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final CreateWorkspaceUseCase createWorkspaceUseCase;
    private final GetWorkspaceByIdUseCase getWorkspaceByIdUseCase;
    private final UpdateWorkspaceUseCase updateWorkspaceUseCase;
    private final DeleteWorkspaceByIdUseCase deleteWorkspaceByIdUseCase;

    @PostMapping
    public ResponseEntity<WorkspaceResponse> createWorkspace(@Valid @RequestBody CreateWorkspaceRequest request) {
        WorkspaceResponse response = createWorkspaceUseCase.createWorkspace(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkspaceResponse> getWorkspaceById(@PathVariable Long id) {
        WorkspaceResponse workspace = getWorkspaceByIdUseCase.getWorkspaceById(id);
        return ResponseEntity.ok(workspace);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<WorkspaceResponse> updateWorkspace(
            @PathVariable Long id,
            @Valid @RequestBody UpdateWorkspaceRequest request) {
        WorkspaceResponse response = updateWorkspaceUseCase.updateWorkspace(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkspace(@PathVariable Long id) {
        deleteWorkspaceByIdUseCase.deleteWorkspace(id);
        return ResponseEntity.noContent().build();
    }
}
