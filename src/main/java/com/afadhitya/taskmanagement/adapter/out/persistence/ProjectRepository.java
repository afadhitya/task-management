package com.afadhitya.taskmanagement.adapter.out.persistence;

import com.afadhitya.taskmanagement.domain.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByWorkspaceId(Long workspaceId);

    @Query("""
            SELECT p FROM Project p
            WHERE p.workspace.id = :workspaceId
            AND (:query IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', CAST(:query AS string), '%'))
                 OR LOWER(p.description) LIKE LOWER(CONCAT('%', CAST(:query AS string), '%')))
            """)
    List<Project> searchByWorkspaceId(@Param("workspaceId") Long workspaceId, @Param("query") String query);

    int countByWorkspaceId(Long workspaceId);
}
