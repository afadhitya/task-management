package com.afadhitya.taskmanagement.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CreateWorkspaceRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Slug is required")
        String slug,

        String logoUrl
) {
}
