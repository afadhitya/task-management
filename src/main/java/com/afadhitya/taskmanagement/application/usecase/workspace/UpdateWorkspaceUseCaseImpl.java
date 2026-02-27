package com.afadhitya.taskmanagement.application.usecase.workspace;

import com.afadhitya.taskmanagement.application.dto.request.UpdateWorkspaceRequest;
import com.afadhitya.taskmanagement.application.dto.response.WorkspaceResponse;
import com.afadhitya.taskmanagement.application.mapper.WorkspaceMapper;
import com.afadhitya.taskmanagement.application.port.in.workspace.UpdateWorkspaceUseCase;
import com.afadhitya.taskmanagement.application.port.out.workspace.WorkspacePersistencePort;
import com.afadhitya.taskmanagement.domain.entity.Workspace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateWorkspaceUseCaseImpl implements UpdateWorkspaceUseCase {

    private final WorkspacePersistencePort workspacePersistencePort;
    private final WorkspaceMapper workspaceMapper;

    @Override
    public WorkspaceResponse updateWorkspace(Long id, UpdateWorkspaceRequest request) {
        Workspace workspace = workspacePersistencePort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found with id: " + id));

        workspaceMapper.updateEntityFromRequest(request, workspace);

        Workspace updatedWorkspace = workspacePersistencePort.save(workspace);
        return workspaceMapper.toResponse(updatedWorkspace);
    }
}
