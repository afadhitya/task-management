package com.afadhitya.taskmanagement.application.dto.response;

import com.afadhitya.taskmanagement.domain.entity.Project.ProjectStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ProjectResponse(
        Long id,
        String name,
        String description,
        String color,
        ProjectStatus status,
        Long workspaceId,
        String workspaceName,
        Long createdBy,
        String createdByName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
