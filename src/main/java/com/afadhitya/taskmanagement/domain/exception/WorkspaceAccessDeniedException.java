package com.afadhitya.taskmanagement.domain.exception;

import lombok.Getter;

@Getter
public class WorkspaceAccessDeniedException extends RuntimeException {

    private final Long workspaceId;
    private final Long userId;

    public WorkspaceAccessDeniedException(String message) {
        super(message);
        this.workspaceId = null;
        this.userId = null;
    }

    public WorkspaceAccessDeniedException(Long workspaceId, Long userId) {
        super(String.format("User with id %d does not have access to workspace with id %d", userId, workspaceId));
        this.workspaceId = workspaceId;
        this.userId = userId;
    }
}
