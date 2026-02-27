package com.afadhitya.taskmanagement.adapter.out.persistence;

import com.afadhitya.taskmanagement.domain.entity.WorkspaceMember;
import com.afadhitya.taskmanagement.domain.enums.WorkspaceRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, Long> {

    List<WorkspaceMember> findByWorkspaceId(Long workspaceId);

    Optional<WorkspaceMember> findByWorkspaceIdAndUserId(Long workspaceId, Long userId);

    @Query("SELECT wm.role FROM WorkspaceMember wm WHERE wm.workspace.id = :workspaceId AND wm.user.id = :userId")
    Optional<WorkspaceRole> findRoleByWorkspaceIdAndUserId(@Param("workspaceId") Long workspaceId, @Param("userId") Long userId);

    boolean existsByWorkspaceIdAndUserIdAndRole(Long workspaceId, Long userId, WorkspaceRole role);

    boolean existsByWorkspaceIdAndUserId(Long workspaceId, Long userId);
}
