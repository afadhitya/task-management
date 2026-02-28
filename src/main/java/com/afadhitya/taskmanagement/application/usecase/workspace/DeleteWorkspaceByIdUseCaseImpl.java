package com.afadhitya.taskmanagement.application.usecase.workspace;

import com.afadhitya.taskmanagement.application.port.in.workspace.DeleteWorkspaceByIdUseCase;
import com.afadhitya.taskmanagement.application.port.out.workspace.WorkspacePersistencePort;
import com.afadhitya.taskmanagement.application.service.AuditEventPublisher;
import com.afadhitya.taskmanagement.domain.entity.Workspace;
import com.afadhitya.taskmanagement.domain.enums.AuditEntityType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteWorkspaceByIdUseCaseImpl implements DeleteWorkspaceByIdUseCase {

    private final WorkspacePersistencePort workspacePersistencePort;
    private final AuditEventPublisher auditEventPublisher;

    @Override
    public void deleteWorkspace(Long id, Long currentUserId) {
        Workspace workspace = workspacePersistencePort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found with id: " + id));

        auditEventPublisher.publishDelete(
                id,
                currentUserId,
                AuditEntityType.WORKSPACE,
                id,
                Map.of("name", workspace.getName(), "slug", workspace.getSlug())
        );

        workspacePersistencePort.deleteById(id);
    }
}
