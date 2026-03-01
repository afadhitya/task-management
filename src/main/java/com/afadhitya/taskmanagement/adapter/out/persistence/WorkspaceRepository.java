package com.afadhitya.taskmanagement.adapter.out.persistence;

import com.afadhitya.taskmanagement.domain.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    boolean existsBySlug(String slug);

    @Query("SELECT w.planConfigurationId FROM Workspace w WHERE w.id = :workspaceId")
    Long findPlanConfigurationIdById(@Param("workspaceId") Long workspaceId);
}
