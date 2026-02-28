package com.afadhitya.taskmanagement.application.usecase.auditlog;

import com.afadhitya.taskmanagement.application.dto.response.AuditLogResponse;
import com.afadhitya.taskmanagement.application.mapper.AuditLogMapper;
import com.afadhitya.taskmanagement.application.port.in.auditlog.ListAuditLogsUseCase;
import com.afadhitya.taskmanagement.application.port.out.auditlog.AuditLogPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.AuditLog;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListAuditLogsUseCaseImpl implements ListAuditLogsUseCase {

    private final AuditLogPersistencePort auditLogPersistencePort;
    private final AuditLogMapper auditLogMapper;

    @Override
    public Page<AuditLogResponse> listAuditLogs(
            Long workspaceId,
            String entityType,
            String action,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable) {
        Page<AuditLog> auditLogs = auditLogPersistencePort.findByWorkspaceIdWithFilters(
                workspaceId, entityType, action, from, to, pageable);
        return auditLogs.map(auditLogMapper::toResponse);
    }
}
