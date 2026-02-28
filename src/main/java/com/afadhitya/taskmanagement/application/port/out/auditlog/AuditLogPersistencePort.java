package com.afadhitya.taskmanagement.application.port.out.auditlog;

import com.afadhitya.taskmanagement.domain.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface AuditLogPersistencePort {

    AuditLog save(AuditLog auditLog);

    Page<AuditLog> findByWorkspaceIdWithFilters(
            Long workspaceId,
            String entityType,
            String action,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable);
}
