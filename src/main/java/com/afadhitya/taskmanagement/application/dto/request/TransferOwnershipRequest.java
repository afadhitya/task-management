package com.afadhitya.taskmanagement.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record TransferOwnershipRequest(
        @NotNull(message = "New owner user ID is required")
        Long newOwnerId
) {
}
