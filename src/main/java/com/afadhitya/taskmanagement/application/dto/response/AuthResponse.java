package com.afadhitya.taskmanagement.application.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AuthResponse(
        String accessToken,
        String tokenType,
        String refreshToken,
        Long expiresIn,
        UserInfo user
) {
    @Builder
    public record UserInfo(
            Long id,
            String email,
            String fullName,
            String avatarUrl,
            LocalDateTime createdAt
    ) {
    }
}
