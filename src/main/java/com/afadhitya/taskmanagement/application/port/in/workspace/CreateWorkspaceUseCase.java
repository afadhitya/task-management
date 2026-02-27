package com.afadhitya.taskmanagement.application.port.in.workspace;

import com.afadhitya.taskmanagement.application.dto.CreateWorkspaceRequest;
import com.afadhitya.taskmanagement.application.dto.WorkspaceResponse;

public interface CreateWorkspaceUseCase {
    WorkspaceResponse createWorkspace(CreateWorkspaceRequest request);
}
