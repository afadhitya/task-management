package com.afadhitya.taskmanagement.application.port.in.auditlog;

import com.afadhitya.taskmanagement.application.dto.response.AuditLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface ListAuditLogsUseCase {

    Page<AuditLogResponse> listAuditLogs(
            Long workspaceId,
            String entityType,
            String action,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable);
}
