package com.afadhitya.taskmanagement.application.usecase.workspace;

import com.afadhitya.taskmanagement.application.port.in.workspace.DeleteWorkspaceByIdUseCase;
import com.afadhitya.taskmanagement.application.port.out.workspace.WorkspacePersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteWorkspaceByIdUseCaseImpl implements DeleteWorkspaceByIdUseCase {

    private final WorkspacePersistencePort workspacePersistencePort;

    @Override
    public void deleteWorkspace(Long id) {
        if (!workspacePersistencePort.existsById(id)) {
            throw new IllegalArgumentException("Workspace not found with id: " + id);
        }
        workspacePersistencePort.deleteById(id);
    }
}
