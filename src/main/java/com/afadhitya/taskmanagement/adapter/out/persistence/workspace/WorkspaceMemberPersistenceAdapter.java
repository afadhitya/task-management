package com.afadhitya.taskmanagement.adapter.out.persistence.workspace;

import com.afadhitya.taskmanagement.adapter.out.persistence.WorkspaceMemberRepository;
import com.afadhitya.taskmanagement.application.port.out.workspace.WorkspaceMemberPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.WorkspaceMember;
import com.afadhitya.taskmanagement.domain.enums.WorkspaceRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WorkspaceMemberPersistenceAdapter implements WorkspaceMemberPersistencePort {

    private final WorkspaceMemberRepository workspaceMemberRepository;

    @Override
    public WorkspaceMember save(WorkspaceMember workspaceMember) {
        return workspaceMemberRepository.save(workspaceMember);
    }

    @Override
    public List<WorkspaceMember> findByWorkspaceId(Long workspaceId) {
        return workspaceMemberRepository.findByWorkspaceId(workspaceId);
    }

    @Override
    public Optional<WorkspaceMember> findByWorkspaceIdAndUserId(Long workspaceId, Long userId) {
        return workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, userId);
    }

    @Override
    public boolean existsByWorkspaceIdAndUserIdAndRole(Long workspaceId, Long userId, WorkspaceRole role) {
        return workspaceMemberRepository.existsByWorkspaceIdAndUserIdAndRole(workspaceId, userId, role);
    }

    @Override
    public boolean existsByWorkspaceIdAndUserId(Long workspaceId, Long userId) {
        return workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, userId);
    }

    @Override
    public Optional<WorkspaceRole> findRoleByWorkspaceIdAndUserId(Long workspaceId, Long userId) {
        return workspaceMemberRepository.findRoleByWorkspaceIdAndUserId(workspaceId, userId);
    }

    @Override
    public void delete(WorkspaceMember workspaceMember) {
        workspaceMemberRepository.delete(workspaceMember);
    }
}
