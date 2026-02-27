package com.afadhitya.taskmanagement.application.dto.response;

import com.afadhitya.taskmanagement.domain.enums.WorkspaceRole;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record WorkspaceMemberResponse(
        Long id,
        Long userId,
        String email,
        String fullName,
        String avatarUrl,
        WorkspaceRole role,
        Long invitedById,
        String invitedByName,
        LocalDateTime joinedAt
) {
}
