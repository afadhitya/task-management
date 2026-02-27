package com.afadhitya.taskmanagement.application.port.in.workspace;

public interface LeaveWorkspaceUseCase {

    void leaveWorkspace(Long workspaceId, Long currentUserId);
}
