package com.afadhitya.taskmanagement.application.usecase.project;

import com.afadhitya.taskmanagement.application.dto.request.UpdateProjectRequest;
import com.afadhitya.taskmanagement.application.dto.response.ProjectResponse;
import com.afadhitya.taskmanagement.application.mapper.ProjectMapper;
import com.afadhitya.taskmanagement.application.port.in.project.UpdateProjectUseCase;
import com.afadhitya.taskmanagement.application.port.out.project.ProjectPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateProjectUseCaseImpl implements UpdateProjectUseCase {

    private final ProjectPersistencePort projectPersistencePort;
    private final ProjectMapper projectMapper;

    @Override
    public ProjectResponse updateProject(Long id, UpdateProjectRequest request) {
        Project project = projectPersistencePort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + id));

        projectMapper.updateEntityFromRequest(request, project);

        Project updatedProject = projectPersistencePort.save(project);
        return projectMapper.toResponse(updatedProject);
    }
}
