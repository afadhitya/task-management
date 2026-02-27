package com.afadhitya.taskmanagement.application.usecase.workspace;

import com.afadhitya.taskmanagement.application.dto.request.CreateWorkspaceRequest;
import com.afadhitya.taskmanagement.application.dto.response.WorkspaceResponse;
import com.afadhitya.taskmanagement.application.mapper.WorkspaceMapper;
import com.afadhitya.taskmanagement.application.port.in.workspace.CreateWorkspaceUseCase;
import com.afadhitya.taskmanagement.application.port.out.user.UserPersistencePort;
import com.afadhitya.taskmanagement.application.port.out.workspace.WorkspacePersistencePort;
import com.afadhitya.taskmanagement.domain.entity.User;
import com.afadhitya.taskmanagement.domain.entity.Workspace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateWorkspaceUseCaseImpl implements CreateWorkspaceUseCase {

    private final WorkspacePersistencePort workspacePersistencePort;
    private final UserPersistencePort userPersistencePort;
    private final WorkspaceMapper workspaceMapper;

    @Override
    public WorkspaceResponse createWorkspace(CreateWorkspaceRequest request) {
        if (workspacePersistencePort.existsBySlug(request.slug())) {
            throw new IllegalArgumentException("Slug already exists: " + request.slug());
        }

        User owner = userPersistencePort.findById(request.ownerId())
                .orElseThrow(() -> new IllegalArgumentException("Owner not found with id: " + request.ownerId()));

        Workspace workspace = workspaceMapper.toEntity(request);
        workspace.setOwner(owner);

        Workspace savedWorkspace = workspacePersistencePort.save(workspace);
        return workspaceMapper.toResponse(savedWorkspace);
    }
}
