package com.afadhitya.taskmanagement.application.usecase.workspace;

import com.afadhitya.taskmanagement.application.dto.request.UpdateWorkspaceRequest;
import com.afadhitya.taskmanagement.application.dto.response.WorkspaceResponse;
import com.afadhitya.taskmanagement.application.mapper.WorkspaceMapper;
import com.afadhitya.taskmanagement.application.port.in.workspace.UpdateWorkspaceUseCase;
import com.afadhitya.taskmanagement.application.port.out.workspace.WorkspacePersistencePort;
import com.afadhitya.taskmanagement.application.service.AuditEventPublisher;
import com.afadhitya.taskmanagement.domain.entity.Workspace;
import com.afadhitya.taskmanagement.domain.enums.AuditEntityType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateWorkspaceUseCaseImpl implements UpdateWorkspaceUseCase {

    private final WorkspacePersistencePort workspacePersistencePort;
    private final WorkspaceMapper workspaceMapper;
    private final AuditEventPublisher auditEventPublisher;

    @Override
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

        workspaceMapper.updateEntityFromRequest(request, workspace);

        Workspace updatedWorkspace = workspacePersistencePort.save(workspace);

        if (!diff.isEmpty()) {
            auditEventPublisher.publishUpdate(
                    id,
                    currentUserId,
                    AuditEntityType.WORKSPACE,
                    id,
                    diff
            );
        }

        return workspaceMapper.toResponse(updatedWorkspace);
    }
}
