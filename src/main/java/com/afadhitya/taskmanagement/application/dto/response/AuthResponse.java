package com.afadhitya.taskmanagement.application.dto.response;

import java.time.LocalDateTime;

public record AuthResponse(
        String accessToken,
        String tokenType,
        Long expiresIn,
        UserInfo user
) {
    public record UserInfo(
            Long id,
            String email,
            String fullName,
            String avatarUrl,
            LocalDateTime createdAt
    ) {
    }
}
