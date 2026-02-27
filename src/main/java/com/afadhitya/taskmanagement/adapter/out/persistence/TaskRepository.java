package com.afadhitya.taskmanagement.adapter.out.persistence;

import com.afadhitya.taskmanagement.domain.entity.Task;
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
            AND (:search IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')))
            AND (COALESCE(:assigneeIds, NULL) IS NULL OR EXISTS (
                SELECT 1 FROM t.assigneeIds aid WHERE aid IN :assigneeIds
            ))
            """
    )
    Page<Task> findByProjectIdWithFilters(
            @Param("projectId") Long projectId,
            @Param("status") com.afadhitya.taskmanagement.domain.enums.TaskStatus status,
            @Param("priority") com.afadhitya.taskmanagement.domain.enums.TaskPriority priority,
            @Param("parentTaskId") Long parentTaskId,
            @Param("dueDateFrom") LocalDate dueDateFrom,
            @Param("dueDateTo") LocalDate dueDateTo,
            @Param("search") String search,
            @Param("assigneeIds") Set<Long> assigneeIds,
            Pageable pageable
    );
}
