package com.afadhitya.taskmanagement.application.port.in.workspace;

public interface RemoveMemberUseCase {

    void removeMember(Long workspaceId, Long userId, Long currentUserId);
}
