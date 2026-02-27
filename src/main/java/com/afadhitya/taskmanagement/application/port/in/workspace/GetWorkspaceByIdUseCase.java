package com.afadhitya.taskmanagement.application.port.in.workspace;

import com.afadhitya.taskmanagement.application.dto.WorkspaceResponse;

public interface GetWorkspaceByIdUseCase {
    WorkspaceResponse getWorkspaceById(Long id);
}
