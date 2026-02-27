package com.afadhitya.taskmanagement.application.usecase.project;

import com.afadhitya.taskmanagement.application.dto.request.CreateProjectRequest;
import com.afadhitya.taskmanagement.application.dto.response.ProjectResponse;
import com.afadhitya.taskmanagement.application.mapper.ProjectMapper;
import com.afadhitya.taskmanagement.application.port.in.project.CreateProjectUseCase;
import com.afadhitya.taskmanagement.application.port.out.project.ProjectPersistencePort;
import com.afadhitya.taskmanagement.application.port.out.workspace.WorkspacePersistencePort;
import com.afadhitya.taskmanagement.application.port.out.user.UserPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.Project;
import com.afadhitya.taskmanagement.domain.entity.User;
import com.afadhitya.taskmanagement.domain.entity.Workspace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateProjectUseCaseImpl implements CreateProjectUseCase {

    private final ProjectPersistencePort projectPersistencePort;
    private final WorkspacePersistencePort workspacePersistencePort;
    private final UserPersistencePort userPersistencePort;
    private final ProjectMapper projectMapper;

    @Override
    public ProjectResponse createProject(CreateProjectRequest request, Long createdByUserId) {
        Workspace workspace = workspacePersistencePort.findById(request.workspaceId())
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found with id: " + request.workspaceId()));

        User createdBy = userPersistencePort.findById(createdByUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + createdByUserId));

        Project project = Project.builder()
                .name(request.name())
                .description(request.description())
                .color(request.color())
                .workspace(workspace)
                .createdBy(createdBy)
                .build();

        Project savedProject = projectPersistencePort.save(project);

        return projectMapper.toResponse(savedProject);
    }
}
