package com.afadhitya.taskmanagement.application.usecase.label;

import com.afadhitya.taskmanagement.application.dto.response.LabelResponse;
import com.afadhitya.taskmanagement.application.mapper.LabelMapper;
import com.afadhitya.taskmanagement.application.port.in.label.GetLabelsByProjectUseCase;
import com.afadhitya.taskmanagement.application.port.out.label.LabelPersistencePort;
import com.afadhitya.taskmanagement.application.port.out.project.ProjectPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.Label;
import com.afadhitya.taskmanagement.domain.entity.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetLabelsByProjectUseCaseImpl implements GetLabelsByProjectUseCase {

    private final LabelPersistencePort labelPersistencePort;
    private final ProjectPersistencePort projectPersistencePort;
    private final LabelMapper labelMapper;

    @Override
    public List<LabelResponse> getLabelsByProject(Long projectId) {
        Project project = projectPersistencePort.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + projectId));

        Long workspaceId = project.getWorkspace().getId();

        // Get global labels (workspace-level, project_id is null)
        List<Label> globalLabels = labelPersistencePort.findByWorkspaceIdAndProjectIdIsNull(workspaceId);

        // Get project-specific labels
        List<Label> projectLabels = labelPersistencePort.findByProjectId(projectId);

        // Combine both
        List<Label> allLabels = new ArrayList<>();
        allLabels.addAll(globalLabels);
        allLabels.addAll(projectLabels);

        return allLabels.stream()
                .map(labelMapper::toResponse)
                .toList();
    }
}
