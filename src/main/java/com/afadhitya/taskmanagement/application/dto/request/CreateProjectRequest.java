package com.afadhitya.taskmanagement.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.With;

@Builder
public record CreateProjectRequest(
        @NotBlank(message = "Name is required")
        String name,

        String description,

        String color,

        @NotNull(message = "Workspace ID is required")
        @With
        Long workspaceId
) {
}
