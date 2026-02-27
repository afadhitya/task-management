package com.afadhitya.taskmanagement.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Email(message = "Email must be valid")
        String email,

        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,

        String fullName,

        String avatarUrl,

        Boolean isActive
) {
}
