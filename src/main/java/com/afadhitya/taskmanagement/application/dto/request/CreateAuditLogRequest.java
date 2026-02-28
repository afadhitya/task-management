package com.afadhitya.taskmanagement.application.dto.request;

import com.afadhitya.taskmanagement.domain.enums.AuditAction;
import com.afadhitya.taskmanagement.domain.enums.AuditEntityType;
import lombok.Builder;

import java.util.Map;

@Builder
public record CreateAuditLogRequest(
        Long workspaceId,
        Long actorId,
        AuditEntityType entityType,
        Long entityId,
        AuditAction action,
        Map<String, Object> diff
) {
}
