package com.afadhitya.taskmanagement.application.dto.request;

import lombok.Builder;

@Builder
public record UpdateWorkspaceRequest(
        String name,
        String logoUrl
) {
}
