package com.afadhitya.taskmanagement.application.usecase.audit;

import com.afadhitya.taskmanagement.application.dto.request.CreateProjectRequest;
import com.afadhitya.taskmanagement.application.dto.request.UpdateProjectRequest;
import com.afadhitya.taskmanagement.application.dto.response.ProjectResponse;
import com.afadhitya.taskmanagement.application.port.in.project.CreateProjectUseCase;
import com.afadhitya.taskmanagement.application.port.in.project.DeleteProjectUseCase;
import com.afadhitya.taskmanagement.application.port.in.project.UpdateProjectUseCase;
import com.afadhitya.taskmanagement.application.port.out.project.ProjectPersistencePort;
import com.afadhitya.taskmanagement.application.service.AuditLogService;
import com.afadhitya.taskmanagement.application.usecase.feature.AuditFeatureInterceptor;
import com.afadhitya.taskmanagement.application.usecase.project.CreateProjectUseCaseImpl;
import com.afadhitya.taskmanagement.application.usecase.project.DeleteProjectUseCaseImpl;
import com.afadhitya.taskmanagement.application.usecase.project.UpdateProjectUseCaseImpl;
import com.afadhitya.taskmanagement.domain.entity.Project;
import com.afadhitya.taskmanagement.domain.enums.AuditEntityType;
import com.afadhitya.taskmanagement.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Primary
@RequiredArgsConstructor
public class AuditedProjectUseCases {

    private final ProjectPersistencePort projectPersistencePort;
    private final AuditLogService auditLogService;
    private final AuditFeatureInterceptor auditInterceptor;

    @Service
    @Primary
    @RequiredArgsConstructor
    public class CreateProject implements CreateProjectUseCase {

        private final CreateProjectUseCaseImpl delegate;

        @Override
        @Transactional
        public ProjectResponse createProject(CreateProjectRequest request, Long createdByUserId) {
            ProjectResponse response = delegate.createProject(request, createdByUserId);

            if (!auditInterceptor.shouldAudit(request.workspaceId())) {
                return response;
            }

            auditInterceptor.auditCreate(
                    request.workspaceId(),
                    createdByUserId,
                    AuditEntityType.PROJECT,
                    response.id(),
                    Map.of("name", response.name(), "workspaceId", request.workspaceId())
            );

            return response;
        }
    }

    @Service
    @Primary
    @RequiredArgsConstructor
    public class UpdateProject implements UpdateProjectUseCase {

        private final UpdateProjectUseCaseImpl delegate;

        @Override
        @Transactional
        public ProjectResponse updateProject(Long id, UpdateProjectRequest request) {
            Project project = projectPersistencePort.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + id));

            Long workspaceId = project.getWorkspace().getId();
            boolean shouldAudit = auditInterceptor.shouldAudit(workspaceId);

            Map<String, Object> diff = new HashMap<>();
            if (shouldAudit) {
                if (request.name() != null && !request.name().equals(project.getName())) {
                    diff.put("name", Map.of("old", project.getName(), "new", request.name()));
                }
                if (request.description() != null && !request.description().equals(project.getDescription())) {
                    diff.put("description", Map.of("old", project.getDescription(), "new", request.description()));
                }
                if (request.color() != null && !request.color().equals(project.getColor())) {
                    diff.put("color", Map.of("old", project.getColor(), "new", request.color()));
                }
            }

            ProjectResponse response = delegate.updateProject(id, request);

            if (shouldAudit && !diff.isEmpty()) {
                auditInterceptor.auditUpdate(
                        workspaceId,
                        SecurityUtils.getCurrentUserId(),
                        AuditEntityType.PROJECT,
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
    public class DeleteProject implements DeleteProjectUseCase {

        private final DeleteProjectUseCaseImpl delegate;

        @Override
        @Transactional
        public void deleteProject(Long id) {
            Project project = projectPersistencePort.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + id));

            Long workspaceId = project.getWorkspace().getId();

            if (auditInterceptor.shouldAudit(workspaceId)) {
                auditInterceptor.auditDelete(
                        workspaceId,
                        SecurityUtils.getCurrentUserId(),
                        AuditEntityType.PROJECT,
                        id,
                        Map.of("name", project.getName(), "workspaceId", workspaceId)
                );
            }

            delegate.deleteProject(id);
        }
    }
}
