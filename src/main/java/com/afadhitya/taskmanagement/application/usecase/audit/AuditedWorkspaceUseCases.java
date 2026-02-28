package com.afadhitya.taskmanagement.application.usecase.audit;

import com.afadhitya.taskmanagement.application.dto.request.CreateWorkspaceRequest;
import com.afadhitya.taskmanagement.application.dto.request.InviteMemberRequest;
import com.afadhitya.taskmanagement.application.dto.request.UpdateMemberRoleRequest;
import com.afadhitya.taskmanagement.application.dto.request.UpdateWorkspaceRequest;
import com.afadhitya.taskmanagement.application.dto.response.WorkspaceMemberResponse;
import com.afadhitya.taskmanagement.application.dto.response.WorkspaceResponse;
import com.afadhitya.taskmanagement.application.port.in.workspace.CreateWorkspaceUseCase;
import com.afadhitya.taskmanagement.application.port.in.workspace.DeleteWorkspaceByIdUseCase;
import com.afadhitya.taskmanagement.application.port.in.workspace.InviteMemberUseCase;
import com.afadhitya.taskmanagement.application.port.in.workspace.LeaveWorkspaceUseCase;
import com.afadhitya.taskmanagement.application.port.in.workspace.RemoveMemberUseCase;
import com.afadhitya.taskmanagement.application.port.in.workspace.UpdateMemberRoleUseCase;
import com.afadhitya.taskmanagement.application.port.in.workspace.UpdateWorkspaceUseCase;
import com.afadhitya.taskmanagement.application.port.out.workspace.WorkspaceMemberPersistencePort;
import com.afadhitya.taskmanagement.application.port.out.workspace.WorkspacePersistencePort;
import com.afadhitya.taskmanagement.application.service.AuditLogService;
import com.afadhitya.taskmanagement.application.usecase.feature.AuditFeatureInterceptor;
import com.afadhitya.taskmanagement.application.usecase.workspace.CreateWorkspaceUseCaseImpl;
import com.afadhitya.taskmanagement.application.usecase.workspace.DeleteWorkspaceByIdUseCaseImpl;
import com.afadhitya.taskmanagement.application.usecase.workspace.InviteMemberUseCaseImpl;
import com.afadhitya.taskmanagement.application.usecase.workspace.LeaveWorkspaceUseCaseImpl;
import com.afadhitya.taskmanagement.application.usecase.workspace.RemoveMemberUseCaseImpl;
import com.afadhitya.taskmanagement.application.usecase.workspace.UpdateMemberRoleUseCaseImpl;
import com.afadhitya.taskmanagement.application.usecase.workspace.UpdateWorkspaceUseCaseImpl;
import com.afadhitya.taskmanagement.domain.entity.Workspace;
import com.afadhitya.taskmanagement.domain.entity.WorkspaceMember;
import com.afadhitya.taskmanagement.domain.enums.AuditAction;
import com.afadhitya.taskmanagement.domain.enums.AuditEntityType;
import com.afadhitya.taskmanagement.domain.enums.WorkspaceRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Primary
@RequiredArgsConstructor
public class AuditedWorkspaceUseCases {

    private final WorkspacePersistencePort workspacePersistencePort;
    private final WorkspaceMemberPersistencePort workspaceMemberPersistencePort;
    private final AuditLogService auditLogService;
    private final AuditFeatureInterceptor auditInterceptor;

    @Service
    @Primary
    @RequiredArgsConstructor
    public class CreateWorkspace implements CreateWorkspaceUseCase {

        private final CreateWorkspaceUseCaseImpl delegate;

        @Override
        @Transactional
        public WorkspaceResponse createWorkspace(CreateWorkspaceRequest request, Long ownerId) {
            WorkspaceResponse response = delegate.createWorkspace(request, ownerId);

            if (!auditInterceptor.shouldAudit(response.id())) {
                return response;
            }

            auditInterceptor.auditCreate(
                    response.id(),
                    ownerId,
                    AuditEntityType.WORKSPACE,
                    response.id(),
                    Map.of("name", response.name(), "slug", response.slug())
            );

            return response;
        }
    }

    @Service
    @Primary
    @RequiredArgsConstructor
    public class UpdateWorkspace implements UpdateWorkspaceUseCase {

        private final UpdateWorkspaceUseCaseImpl delegate;

        @Override
        @Transactional
        public WorkspaceResponse updateWorkspace(Long id, UpdateWorkspaceRequest request, Long currentUserId) {
            Workspace workspace = workspacePersistencePort.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Workspace not found with id: " + id));

            boolean shouldAudit = auditInterceptor.shouldAudit(id);

            Map<String, Object> diff = new HashMap<>();
            if (shouldAudit) {
                if (request.name() != null && !request.name().equals(workspace.getName())) {
                    diff.put("name", Map.of("old", workspace.getName(), "new", request.name()));
                }
                if (request.logoUrl() != null && !request.logoUrl().equals(workspace.getLogoUrl())) {
                    diff.put("logoUrl", Map.of("old", workspace.getLogoUrl(), "new", request.logoUrl()));
                }
            }

            WorkspaceResponse response = delegate.updateWorkspace(id, request, currentUserId);

            if (shouldAudit && !diff.isEmpty()) {
                auditInterceptor.auditUpdate(
                        id,
                        currentUserId,
                        AuditEntityType.WORKSPACE,
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
    public class DeleteWorkspace implements DeleteWorkspaceByIdUseCase {

        private final DeleteWorkspaceByIdUseCaseImpl delegate;

        @Override
        @Transactional
        public void deleteWorkspace(Long id, Long currentUserId) {
            Workspace workspace = workspacePersistencePort.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Workspace not found with id: " + id));

            if (auditInterceptor.shouldAudit(id)) {
                auditInterceptor.auditDelete(
                        id,
                        currentUserId,
                        AuditEntityType.WORKSPACE,
                        id,
                        Map.of("name", workspace.getName(), "slug", workspace.getSlug())
                );
            }

            delegate.deleteWorkspace(id, currentUserId);
        }
    }

    @Service
    @Primary
    @RequiredArgsConstructor
    public class InviteMember implements InviteMemberUseCase {

        private final InviteMemberUseCaseImpl delegate;

        @Override
        @Transactional
        public WorkspaceMemberResponse inviteMember(Long workspaceId, InviteMemberRequest request, Long currentUserId) {
            WorkspaceMemberResponse response = delegate.inviteMember(workspaceId, request, currentUserId);

            if (!auditInterceptor.shouldAudit(workspaceId)) {
                return response;
            }

            auditInterceptor.audit(
                    workspaceId,
                    currentUserId,
                    AuditEntityType.WORKSPACE,
                    workspaceId,
                    AuditAction.MEMBER_INVITE,
                    Map.of(
                            "invitedUserId", response.userId(),
                            "invitedUserEmail", response.email(),
                            "role", response.role().name()
                    )
            );

            return response;
        }
    }

    @Service
    @Primary
    @RequiredArgsConstructor
    public class RemoveMember implements RemoveMemberUseCase {

        private final RemoveMemberUseCaseImpl delegate;

        @Override
        @Transactional
        public void removeMember(Long workspaceId, Long userId, Long currentUserId) {
            WorkspaceMember member = workspaceMemberPersistencePort.findByWorkspaceIdAndUserId(workspaceId, userId)
                    .orElseThrow(() -> new IllegalArgumentException("Member not found"));

            if (auditInterceptor.shouldAudit(workspaceId)) {
                auditInterceptor.audit(
                        workspaceId,
                        currentUserId,
                        AuditEntityType.WORKSPACE,
                        workspaceId,
                        AuditAction.MEMBER_REMOVE,
                        Map.of(
                                "removedUserId", userId,
                                "removedUserEmail", member.getUser().getEmail(),
                                "role", member.getRole().name()
                        )
                );
            }

            delegate.removeMember(workspaceId, userId, currentUserId);
        }
    }

    @Service
    @Primary
    @RequiredArgsConstructor
    public class UpdateMemberRole implements UpdateMemberRoleUseCase {

        private final UpdateMemberRoleUseCaseImpl delegate;

        @Override
        @Transactional
        public WorkspaceMemberResponse updateMemberRole(Long workspaceId, Long userId, UpdateMemberRoleRequest request, Long currentUserId) {
            WorkspaceMember member = workspaceMemberPersistencePort.findByWorkspaceIdAndUserId(workspaceId, userId)
                    .orElseThrow(() -> new IllegalArgumentException("Member not found"));

            boolean shouldAudit = auditInterceptor.shouldAudit(workspaceId);
            WorkspaceRole oldRole = shouldAudit ? member.getRole() : null;

            WorkspaceMemberResponse response = delegate.updateMemberRole(workspaceId, userId, request, currentUserId);

            if (shouldAudit) {
                auditInterceptor.audit(
                        workspaceId,
                        currentUserId,
                        AuditEntityType.WORKSPACE,
                        workspaceId,
                        AuditAction.MEMBER_UPDATE,
                        Map.of(
                                "userId", userId,
                                "userEmail", member.getUser().getEmail(),
                                "oldRole", oldRole.name(),
                                "newRole", request.role().name()
                        )
                );
            }

            return response;
        }
    }

    @Service
    @Primary
    @RequiredArgsConstructor
    public class LeaveWorkspace implements LeaveWorkspaceUseCase {

        private final LeaveWorkspaceUseCaseImpl delegate;

        @Override
        @Transactional
        public void leaveWorkspace(Long workspaceId, Long currentUserId) {
            WorkspaceMember member = workspaceMemberPersistencePort.findByWorkspaceIdAndUserId(workspaceId, currentUserId)
                    .orElseThrow(() -> new IllegalArgumentException("Membership not found"));

            if (auditInterceptor.shouldAudit(workspaceId)) {
                auditInterceptor.audit(
                        workspaceId,
                        currentUserId,
                        AuditEntityType.WORKSPACE,
                        workspaceId,
                        AuditAction.MEMBER_REMOVE,
                        Map.of(
                                "userId", currentUserId,
                                "userEmail", member.getUser().getEmail(),
                                "role", member.getRole().name(),
                                "actionType", "SELF_LEAVE"
                        )
                );
            }

            delegate.leaveWorkspace(workspaceId, currentUserId);
        }
    }
}
