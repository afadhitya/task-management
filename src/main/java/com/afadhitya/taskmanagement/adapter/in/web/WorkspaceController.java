package com.afadhitya.taskmanagement.adapter.in.web;

import com.afadhitya.taskmanagement.application.dto.request.CreateWorkspaceRequest;
import com.afadhitya.taskmanagement.application.dto.request.InviteMemberRequest;
import com.afadhitya.taskmanagement.application.dto.request.TransferOwnershipRequest;
import com.afadhitya.taskmanagement.application.dto.request.UpdateMemberRoleRequest;
import com.afadhitya.taskmanagement.application.dto.request.UpdateWorkspaceRequest;
import com.afadhitya.taskmanagement.application.dto.response.WorkspaceMemberResponse;
import com.afadhitya.taskmanagement.application.dto.response.WorkspaceResponse;
import com.afadhitya.taskmanagement.application.port.in.workspace.CreateWorkspaceUseCase;
import com.afadhitya.taskmanagement.application.port.in.workspace.DeleteWorkspaceByIdUseCase;
import com.afadhitya.taskmanagement.application.port.in.workspace.GetWorkspaceByIdUseCase;
import com.afadhitya.taskmanagement.application.port.in.workspace.GetWorkspaceMembersUseCase;
import com.afadhitya.taskmanagement.application.port.in.workspace.InviteMemberUseCase;
import com.afadhitya.taskmanagement.application.port.in.workspace.LeaveWorkspaceUseCase;
import com.afadhitya.taskmanagement.application.port.in.workspace.RemoveMemberUseCase;
import com.afadhitya.taskmanagement.application.port.in.workspace.TransferOwnershipUseCase;
import com.afadhitya.taskmanagement.application.port.in.workspace.UpdateMemberRoleUseCase;
import com.afadhitya.taskmanagement.application.port.in.workspace.UpdateWorkspaceUseCase;
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
@RequestMapping("/workspaces")
@RequiredArgsConstructor
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class WorkspaceController {

    private final CreateWorkspaceUseCase createWorkspaceUseCase;
    private final GetWorkspaceByIdUseCase getWorkspaceByIdUseCase;
    private final UpdateWorkspaceUseCase updateWorkspaceUseCase;
    private final DeleteWorkspaceByIdUseCase deleteWorkspaceByIdUseCase;
    private final GetWorkspaceMembersUseCase getWorkspaceMembersUseCase;
    private final InviteMemberUseCase inviteMemberUseCase;
    private final UpdateMemberRoleUseCase updateMemberRoleUseCase;
    private final TransferOwnershipUseCase transferOwnershipUseCase;
    private final RemoveMemberUseCase removeMemberUseCase;
    private final LeaveWorkspaceUseCase leaveWorkspaceUseCase;

    @PostMapping
    public ResponseEntity<WorkspaceResponse> createWorkspace(@Valid @RequestBody CreateWorkspaceRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        WorkspaceResponse response = createWorkspaceUseCase.createWorkspace(request, currentUserId);
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
        Long currentUserId = SecurityUtils.getCurrentUserId();
        WorkspaceResponse response = updateWorkspaceUseCase.updateWorkspace(id, request, currentUserId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkspace(@PathVariable Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        deleteWorkspaceByIdUseCase.deleteWorkspace(id, currentUserId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<WorkspaceMemberResponse>> getWorkspaceMembers(@PathVariable Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<WorkspaceMemberResponse> members = getWorkspaceMembersUseCase.getWorkspaceMembers(id, currentUserId);
        return ResponseEntity.ok(members);
    }

    @PostMapping("/{id}/members/invite")
    public ResponseEntity<WorkspaceMemberResponse> inviteMember(
            @PathVariable Long id,
            @Valid @RequestBody InviteMemberRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        WorkspaceMemberResponse response = inviteMemberUseCase.inviteMember(id, request, currentUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/members/{userId}")
    public ResponseEntity<WorkspaceMemberResponse> updateMemberRole(
            @PathVariable Long id,
            @PathVariable Long userId,
            @Valid @RequestBody UpdateMemberRoleRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        WorkspaceMemberResponse response = updateMemberRoleUseCase.updateMemberRole(id, userId, request, currentUserId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/transfer-ownership")
    public ResponseEntity<WorkspaceMemberResponse> transferOwnership(
            @PathVariable Long id,
            @Valid @RequestBody TransferOwnershipRequest request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        WorkspaceMemberResponse response = transferOwnershipUseCase.transferOwnership(id, request, currentUserId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long id,
            @PathVariable Long userId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        removeMemberUseCase.removeMember(id, userId, currentUserId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<Void> leaveWorkspace(@PathVariable Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        leaveWorkspaceUseCase.leaveWorkspace(id, currentUserId);
        return ResponseEntity.noContent().build();
    }
}
