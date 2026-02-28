package com.afadhitya.taskmanagement.adapter.out.persistence.auditlog;

import com.afadhitya.taskmanagement.domain.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query("SELECT al FROM AuditLog al WHERE al.workspace.id = :workspaceId " +
           "AND (:entityType IS NULL OR al.entityType = :entityType) " +
           "AND (:action IS NULL OR al.action = :action) " +
           "AND (:from IS NULL OR al.createdAt >= :from) " +
           "AND (:to IS NULL OR al.createdAt <= :to) " +
           "ORDER BY al.createdAt DESC")
    Page<AuditLog> findByWorkspaceIdWithFilters(
            @Param("workspaceId") Long workspaceId,
            @Param("entityType") String entityType,
            @Param("action") String action,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable);
}
