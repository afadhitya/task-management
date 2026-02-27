package com.afadhitya.taskmanagement.application.port.in.workspace;

import com.afadhitya.taskmanagement.application.dto.request.CreateWorkspaceRequest;
import com.afadhitya.taskmanagement.application.dto.response.WorkspaceResponse;

public interface CreateWorkspaceUseCase {
    WorkspaceResponse createWorkspace(CreateWorkspaceRequest request);
}
