package com.afadhitya.taskmanagement.application.port.out.workspace;

import com.afadhitya.taskmanagement.domain.entity.WorkspaceMember;
import com.afadhitya.taskmanagement.domain.enums.WorkspaceRole;

import java.util.List;
import java.util.Optional;

public interface WorkspaceMemberPersistencePort {

    WorkspaceMember save(WorkspaceMember workspaceMember);

    List<WorkspaceMember> findByWorkspaceId(Long workspaceId);

    Optional<WorkspaceMember> findByWorkspaceIdAndUserId(Long workspaceId, Long userId);

    Optional<WorkspaceRole> findRoleByWorkspaceIdAndUserId(Long workspaceId, Long userId);

    boolean existsByWorkspaceIdAndUserIdAndRole(Long workspaceId, Long userId, WorkspaceRole role);

    boolean existsByWorkspaceIdAndUserId(Long workspaceId, Long userId);

    void delete(WorkspaceMember workspaceMember);
}
