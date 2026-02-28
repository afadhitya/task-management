package com.afadhitya.taskmanagement.application.usecase.project;

import com.afadhitya.taskmanagement.application.port.in.project.DeleteProjectUseCase;
import com.afadhitya.taskmanagement.application.port.out.project.ProjectPersistencePort;
import com.afadhitya.taskmanagement.application.service.AuditEventPublisher;
import com.afadhitya.taskmanagement.domain.entity.Project;
import com.afadhitya.taskmanagement.domain.enums.AuditEntityType;
import com.afadhitya.taskmanagement.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteProjectUseCaseImpl implements DeleteProjectUseCase {

    private final ProjectPersistencePort projectPersistencePort;
    private final AuditEventPublisher auditEventPublisher;

    @Override
    public void deleteProject(Long id) {
        Project project = projectPersistencePort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + id));

        Long workspaceId = project.getWorkspace().getId();

        auditEventPublisher.publishDelete(
                workspaceId,
                SecurityUtils.getCurrentUserId(),
                AuditEntityType.PROJECT,
                id,
                Map.of("name", project.getName(), "workspaceId", workspaceId)
        );

        projectPersistencePort.deleteById(id);
    }
}
