package com.afadhitya.taskmanagement.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record LogoutRequest(
        @NotNull(message = "User ID is required")
        Long userId
) {
}
