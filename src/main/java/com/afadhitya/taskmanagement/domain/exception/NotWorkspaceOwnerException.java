package com.afadhitya.taskmanagement.domain.exception;

public class NotWorkspaceOwnerException extends RuntimeException {

    public NotWorkspaceOwnerException(String message) {
        super(message);
    }

    public NotWorkspaceOwnerException(Long workspaceId, Long userId) {
        super(String.format("User with id %d is not the owner of workspace with id %d", userId, workspaceId));
    }
}
