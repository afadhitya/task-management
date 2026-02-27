package com.afadhitya.taskmanagement.application.usecase.project;

import com.afadhitya.taskmanagement.application.dto.response.ProjectResponse;
import com.afadhitya.taskmanagement.application.mapper.ProjectMapper;
import com.afadhitya.taskmanagement.application.port.in.project.GetProjectByIdUseCase;
import com.afadhitya.taskmanagement.application.port.out.project.ProjectPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetProjectByIdUseCaseImpl implements GetProjectByIdUseCase {

    private final ProjectPersistencePort projectPersistencePort;
    private final ProjectMapper projectMapper;

    @Override
    public ProjectResponse getProjectById(Long id) {
        Project project = projectPersistencePort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + id));
        return projectMapper.toResponse(project);
    }
}
