package com.afadhitya.taskmanagement.application.dto;

public record UpdateWorkspaceRequest(
        String name,
        String logoUrl
) {
}
