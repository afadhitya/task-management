package com.afadhitya.taskmanagement.application.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
public record AuditLogResponse(
        Long id,
        Long workspaceId,
        Long actorId,
        String actorFullName,
        String action,
        String entityType,
        Long entityId,
        Map<String, Object> diff,
        LocalDateTime createdAt
) {
}
