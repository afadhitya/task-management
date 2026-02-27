package com.afadhitya.taskmanagement.application.port.in.workspace;

import com.afadhitya.taskmanagement.application.dto.request.UpdateWorkspaceRequest;
import com.afadhitya.taskmanagement.application.dto.response.WorkspaceResponse;

public interface UpdateWorkspaceUseCase {
    WorkspaceResponse updateWorkspace(Long id, UpdateWorkspaceRequest request, Long currentUserId);
}
