package com.afadhitya.taskmanagement.application.usecase.label;

import com.afadhitya.taskmanagement.application.dto.request.CreateLabelRequest;
import com.afadhitya.taskmanagement.application.dto.response.LabelResponse;
import com.afadhitya.taskmanagement.application.mapper.LabelMapper;
import com.afadhitya.taskmanagement.application.port.in.label.CreateLabelUseCase;
import com.afadhitya.taskmanagement.application.port.in.project.ProjectPermissionUseCase;
import com.afadhitya.taskmanagement.application.port.in.workspace.WorkspacePermissionUseCase;
import com.afadhitya.taskmanagement.application.port.out.label.LabelPersistencePort;
import com.afadhitya.taskmanagement.application.port.out.project.ProjectPersistencePort;
import com.afadhitya.taskmanagement.application.port.out.user.UserPersistencePort;
import com.afadhitya.taskmanagement.application.port.out.workspace.WorkspacePersistencePort;
import com.afadhitya.taskmanagement.application.service.AuditEventPublisher;
import com.afadhitya.taskmanagement.domain.entity.Label;
import com.afadhitya.taskmanagement.domain.entity.Project;
import com.afadhitya.taskmanagement.domain.entity.User;
import com.afadhitya.taskmanagement.domain.entity.Workspace;
import com.afadhitya.taskmanagement.domain.enums.AuditEntityType;
import com.afadhitya.taskmanagement.domain.enums.WorkspaceRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateLabelUseCaseImpl implements CreateLabelUseCase {

    private final LabelPersistencePort labelPersistencePort;
    private final WorkspacePersistencePort workspacePersistencePort;
    private final ProjectPersistencePort projectPersistencePort;
    private final UserPersistencePort userPersistencePort;
    private final WorkspacePermissionUseCase workspacePermissionUseCase;
    private final ProjectPermissionUseCase projectPermissionUseCase;
    private final LabelMapper labelMapper;
    private final AuditEventPublisher auditEventPublisher;

    @Override
    public LabelResponse createLabel(Long workspaceId, CreateLabelRequest request, Long createdByUserId) {
        User createdBy = userPersistencePort.findById(createdByUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + createdByUserId));

        Workspace workspace = workspacePersistencePort.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found with id: " + workspaceId));

        // Check permission based on label type
        if (request.projectId() == null) {
            // Global label - only OWNER/ADMIN can create
            if (!workspacePermissionUseCase.hasRole(workspaceId, createdByUserId, WorkspaceRole.OWNER, WorkspaceRole.ADMIN)) {
                throw new IllegalStateException("Only workspace owner or admin can create global labels");
            }

            // Check for duplicate global label
            if (labelPersistencePort.existsByWorkspaceIdAndNameAndProjectIdIsNull(workspaceId, request.name())) {
                throw new IllegalArgumentException("Global label with name '" + request.name() + "' already exists");
            }
        } else {
            boolean isProjectManager = projectPermissionUseCase.canManageProject(request.projectId(), createdByUserId);

            if (!isProjectManager) {
                throw new IllegalStateException("Only workspace owner/admin or project manager can create project labels");
            }

            // Check for duplicate project label
            if (labelPersistencePort.existsByProjectIdAndName(request.projectId(), request.name())) {
                throw new IllegalArgumentException("Label with name '" + request.name() + "' already exists in this project");
            }
        }

        Label.LabelBuilder labelBuilder = Label.builder()
                .name(request.name())
                .color(request.color())
                .workspace(workspace)
                .createdBy(createdBy);

        if (request.projectId() != null) {
            Project project = projectPersistencePort.findById(request.projectId())
                    .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + request.projectId()));
            labelBuilder.project(project);
        }

        Label label = labelBuilder.build();
        Label savedLabel = labelPersistencePort.save(label);

        Map<String, Object> newValues = new HashMap<>();
        newValues.put("name", savedLabel.getName());
        newValues.put("color", savedLabel.getColor());
        if (request.projectId() != null) {
            newValues.put("projectId", request.projectId());
        }

        auditEventPublisher.publishCreate(
                workspaceId,
                createdByUserId,
                AuditEntityType.LABEL,
                savedLabel.getId(),
                newValues
        );

        return labelMapper.toResponse(savedLabel);
    }
}
