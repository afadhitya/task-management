package com.afadhitya.taskmanagement.application.usecase.project;

import com.afadhitya.taskmanagement.application.dto.request.UpdateProjectRequest;
import com.afadhitya.taskmanagement.application.dto.response.ProjectResponse;
import com.afadhitya.taskmanagement.application.mapper.ProjectMapper;
import com.afadhitya.taskmanagement.application.port.in.project.UpdateProjectUseCase;
import com.afadhitya.taskmanagement.application.port.out.project.ProjectPersistencePort;
import com.afadhitya.taskmanagement.application.service.AuditEventPublisher;
import com.afadhitya.taskmanagement.domain.entity.Project;
import com.afadhitya.taskmanagement.domain.enums.AuditEntityType;
import com.afadhitya.taskmanagement.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateProjectUseCaseImpl implements UpdateProjectUseCase {

    private final ProjectPersistencePort projectPersistencePort;
    private final ProjectMapper projectMapper;
    private final AuditEventPublisher auditEventPublisher;

    @Override
    public ProjectResponse updateProject(Long id, UpdateProjectRequest request) {
        Project project = projectPersistencePort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + id));

        Map<String, Object> diff = new HashMap<>();
        if (request.name() != null && !request.name().equals(project.getName())) {
            diff.put("name", Map.of("old", project.getName(), "new", request.name()));
        }
        if (request.description() != null && !request.description().equals(project.getDescription())) {
            diff.put("description", Map.of("old", project.getDescription(), "new", request.description()));
        }
        if (request.color() != null && !request.color().equals(project.getColor())) {
            diff.put("color", Map.of("old", project.getColor(), "new", request.color()));
        }

        projectMapper.updateEntityFromRequest(request, project);

        Project updatedProject = projectPersistencePort.save(project);

        if (!diff.isEmpty()) {
            auditEventPublisher.publishUpdate(
                    project.getWorkspace().getId(),
                    SecurityUtils.getCurrentUserId(),
                    AuditEntityType.PROJECT,
                    id,
                    diff
            );
        }

        return projectMapper.toResponse(updatedProject);
    }
}
