package com.afadhitya.taskmanagement.application.usecase.audit;

import com.afadhitya.taskmanagement.application.dto.request.CreateWorkspaceRequest;
import com.afadhitya.taskmanagement.application.dto.request.UpdateWorkspaceRequest;
import com.afadhitya.taskmanagement.application.dto.response.WorkspaceResponse;
import com.afadhitya.taskmanagement.application.port.in.workspace.CreateWorkspaceUseCase;
import com.afadhitya.taskmanagement.application.port.in.workspace.DeleteWorkspaceByIdUseCase;
import com.afadhitya.taskmanagement.application.port.in.workspace.UpdateWorkspaceUseCase;
import com.afadhitya.taskmanagement.application.port.out.workspace.WorkspacePersistencePort;
import com.afadhitya.taskmanagement.application.service.AuditLogService;
import com.afadhitya.taskmanagement.application.usecase.workspace.CreateWorkspaceUseCaseImpl;
import com.afadhitya.taskmanagement.application.usecase.workspace.DeleteWorkspaceByIdUseCaseImpl;
import com.afadhitya.taskmanagement.application.usecase.workspace.UpdateWorkspaceUseCaseImpl;
import com.afadhitya.taskmanagement.domain.entity.Workspace;
import com.afadhitya.taskmanagement.domain.enums.AuditEntityType;
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
    private final AuditLogService auditLogService;

    @Service
    @Primary
    @RequiredArgsConstructor
    public class CreateWorkspace implements CreateWorkspaceUseCase {

        private final CreateWorkspaceUseCaseImpl delegate;

        @Override
        @Transactional
        public WorkspaceResponse createWorkspace(CreateWorkspaceRequest request, Long ownerId) {
            WorkspaceResponse response = delegate.createWorkspace(request, ownerId);

            auditLogService.createCreate(
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

            Map<String, Object> diff = new HashMap<>();
            if (request.name() != null && !request.name().equals(workspace.getName())) {
                diff.put("name", Map.of("old", workspace.getName(), "new", request.name()));
            }
            if (request.logoUrl() != null && !request.logoUrl().equals(workspace.getLogoUrl())) {
                diff.put("logoUrl", Map.of("old", workspace.getLogoUrl(), "new", request.logoUrl()));
            }

            WorkspaceResponse response = delegate.updateWorkspace(id, request, currentUserId);

            if (!diff.isEmpty()) {
                auditLogService.createUpdate(
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

            auditLogService.createDelete(
                    id,
                    currentUserId,
                    AuditEntityType.WORKSPACE,
                    id,
                    Map.of("name", workspace.getName(), "slug", workspace.getSlug())
            );

            delegate.deleteWorkspace(id, currentUserId);
        }
    }
}
