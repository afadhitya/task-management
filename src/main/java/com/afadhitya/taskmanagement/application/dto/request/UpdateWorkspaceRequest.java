package com.afadhitya.taskmanagement.application.dto.request;

public record UpdateWorkspaceRequest(
        String name,
        String logoUrl
) {
}
