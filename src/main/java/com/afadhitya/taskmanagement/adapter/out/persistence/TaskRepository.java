package com.afadhitya.taskmanagement.adapter.out.persistence;

import com.afadhitya.taskmanagement.domain.entity.Task;
import com.afadhitya.taskmanagement.domain.enums.TaskPriority;
import com.afadhitya.taskmanagement.domain.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjectId(Long projectId);

    List<Task> findByParentTaskId(Long parentTaskId);

    @Query("""
            SELECT t FROM Task t
            WHERE t.project.id = :projectId
            AND (:status IS NULL OR t.status = :status)
            AND (:priority IS NULL OR t.priority = :priority)
            AND (:parentTaskId IS NULL OR t.parentTask.id = :parentTaskId)
            AND (:dueDateFrom IS NULL OR t.dueDate >= :dueDateFrom)
            AND (:dueDateTo IS NULL OR t.dueDate <= :dueDateTo)
            AND (:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')))
            AND (COALESCE(:assigneeIds, NULL) IS NULL OR EXISTS (
                SELECT 1 FROM t.assigneeIds aid WHERE aid IN :assigneeIds
            ))
            """
    )
    Page<Task> findByProjectIdWithFilters(
            @Param("projectId") Long projectId,
            @Param("status") TaskStatus status,
            @Param("priority") TaskPriority priority,
            @Param("parentTaskId") Long parentTaskId,
            @Param("dueDateFrom") LocalDate dueDateFrom,
            @Param("dueDateTo") LocalDate dueDateTo,
            @Param("search") String search,
            @Param("assigneeIds") Set<Long> assigneeIds,
            Pageable pageable
    );

    @Query("SELECT t FROM Task t WHERE :assigneeId MEMBER OF t.assigneeIds")
    List<Task> findByAssigneeId(@Param("assigneeId") Long assigneeId);

    @Query("""
            SELECT t FROM Task t
            WHERE t.project.workspace.id = :workspaceId
            AND (:query IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', CAST(:query AS string), '%'))
                 OR LOWER(t.description) LIKE LOWER(CONCAT('%', CAST(:query AS string), '%')))
            """)
    List<Task> searchByWorkspaceId(@Param("workspaceId") Long workspaceId, @Param("query") String query);
}
