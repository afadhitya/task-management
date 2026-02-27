package com.afadhitya.taskmanagement.application.port.in.workspace;

import com.afadhitya.taskmanagement.application.dto.UpdateWorkspaceRequest;
import com.afadhitya.taskmanagement.application.dto.WorkspaceResponse;

public interface UpdateWorkspaceUseCase {
    WorkspaceResponse updateWorkspace(Long id, UpdateWorkspaceRequest request);
}
