package com.afadhitya.taskmanagement.application.usecase.label;

import com.afadhitya.taskmanagement.application.dto.request.UpdateLabelRequest;
import com.afadhitya.taskmanagement.application.dto.response.LabelResponse;
import com.afadhitya.taskmanagement.application.mapper.LabelMapper;
import com.afadhitya.taskmanagement.application.port.in.label.UpdateLabelUseCase;
import com.afadhitya.taskmanagement.application.port.in.project.ProjectPermissionUseCase;
import com.afadhitya.taskmanagement.application.port.in.workspace.WorkspacePermissionUseCase;
import com.afadhitya.taskmanagement.application.port.out.label.LabelPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.Label;
import com.afadhitya.taskmanagement.domain.enums.WorkspaceRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateLabelUseCaseImpl implements UpdateLabelUseCase {

    private final LabelPersistencePort labelPersistencePort;
    private final WorkspacePermissionUseCase workspacePermissionUseCase;
    private final ProjectPermissionUseCase projectPermissionUseCase;
    private final LabelMapper labelMapper;

    @Override
    public LabelResponse updateLabel(Long labelId, UpdateLabelRequest request, Long currentUserId) {
        Label label = labelPersistencePort.findById(labelId)
                .orElseThrow(() -> new IllegalArgumentException("Label not found with id: " + labelId));

        Long workspaceId = label.getWorkspace().getId();

        // Check permission based on label type
        if (label.isGlobal()) {
            // Global label - only OWNER/ADMIN can update
            if (!workspacePermissionUseCase.hasRole(workspaceId, currentUserId, WorkspaceRole.OWNER, WorkspaceRole.ADMIN)) {
                throw new IllegalStateException("Only workspace owner or admin can update global labels");
            }
        } else {
            Long projectId = label.getProject().getId();
            boolean isProjectManager = projectPermissionUseCase.canManageProject(projectId, currentUserId);

            if (!isProjectManager) {
                throw new IllegalStateException("Only workspace owner/admin or project manager can update project labels");
            }
        }

        labelMapper.updateEntityFromRequest(request, label);

        Label updatedLabel = labelPersistencePort.save(label);
        return labelMapper.toResponse(updatedLabel);
    }
}
