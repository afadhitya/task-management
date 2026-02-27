package com.afadhitya.taskmanagement.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateWorkspaceRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Slug is required")
        String slug,

        String logoUrl,

        @NotNull(message = "Owner ID is required")
        Long ownerId
) {
}
