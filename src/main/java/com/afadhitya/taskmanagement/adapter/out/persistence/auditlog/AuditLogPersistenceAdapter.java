package com.afadhitya.taskmanagement.adapter.out.persistence.auditlog;

import com.afadhitya.taskmanagement.application.port.out.auditlog.AuditLogPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.AuditLog;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AuditLogPersistenceAdapter implements AuditLogPersistencePort {

    private final AuditLogRepository auditLogRepository;

    @Override
    public AuditLog save(AuditLog auditLog) {
        return auditLogRepository.save(auditLog);
    }

    @Override
    public Page<AuditLog> findByWorkspaceIdWithFilters(
            Long workspaceId,
            String entityType,
            String action,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable) {
        return auditLogRepository.findByWorkspaceIdWithFilters(
                workspaceId, entityType, action, from, to, pageable);
    }
}
