package com.afadhitya.taskmanagement.application.dto.request;

import com.afadhitya.taskmanagement.domain.entity.Project.ProjectStatus;
import lombok.Builder;

@Builder
public record UpdateProjectRequest(
        String name,
        String description,
        String color,
        ProjectStatus status
) {
}
