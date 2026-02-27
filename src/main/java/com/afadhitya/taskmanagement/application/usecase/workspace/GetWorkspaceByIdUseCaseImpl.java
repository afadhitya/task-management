package com.afadhitya.taskmanagement.application.usecase.workspace;

import com.afadhitya.taskmanagement.application.dto.WorkspaceResponse;
import com.afadhitya.taskmanagement.application.mapper.WorkspaceMapper;
import com.afadhitya.taskmanagement.application.port.in.workspace.GetWorkspaceByIdUseCase;
import com.afadhitya.taskmanagement.application.port.out.workspace.WorkspacePersistencePort;
import com.afadhitya.taskmanagement.domain.entity.Workspace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetWorkspaceByIdUseCaseImpl implements GetWorkspaceByIdUseCase {

    private final WorkspacePersistencePort workspacePersistencePort;
    private final WorkspaceMapper workspaceMapper;

    @Override
    public WorkspaceResponse getWorkspaceById(Long id) {
        Workspace workspace = workspacePersistencePort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found with id: " + id));
        return workspaceMapper.toResponse(workspace);
    }
}
