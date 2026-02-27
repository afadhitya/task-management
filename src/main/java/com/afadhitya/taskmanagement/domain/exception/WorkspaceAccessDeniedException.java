package com.afadhitya.taskmanagement.domain.exception;

public class WorkspaceAccessDeniedException extends RuntimeException {

    public WorkspaceAccessDeniedException(String message) {
        super(message);
    }

    public WorkspaceAccessDeniedException(Long workspaceId, Long userId) {
        super(String.format("User with id %d does not have access to workspace with id %d", userId, workspaceId));
    }
}
