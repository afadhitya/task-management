package com.afadhitya.taskmanagement.application.usecase.project;

import com.afadhitya.taskmanagement.application.dto.response.ProjectResponse;
import com.afadhitya.taskmanagement.application.mapper.ProjectMapper;
import com.afadhitya.taskmanagement.application.port.in.project.GetProjectsByWorkspaceUseCase;
import com.afadhitya.taskmanagement.application.port.out.project.ProjectPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetProjectsByWorkspaceUseCaseImpl implements GetProjectsByWorkspaceUseCase {

    private final ProjectPersistencePort projectPersistencePort;
    private final ProjectMapper projectMapper;

    @Override
    public List<ProjectResponse> getProjectsByWorkspace(Long workspaceId) {
        List<Project> projects = projectPersistencePort.findByWorkspaceId(workspaceId);
        return projects.stream()
                .map(projectMapper::toResponse)
                .toList();
    }
}
