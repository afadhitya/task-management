package com.afadhitya.taskmanagement.application.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LabelResponse(
        Long id,
        String name,
        String color,
        Long workspaceId,
        String workspaceName,
        Long projectId,
        String projectName,
        boolean isGlobal,
        Long createdBy,
        String createdByName,
        LocalDateTime createdAt
) {
}
