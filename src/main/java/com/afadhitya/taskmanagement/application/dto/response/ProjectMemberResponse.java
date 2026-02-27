package com.afadhitya.taskmanagement.application.dto.response;

import com.afadhitya.taskmanagement.domain.enums.ProjectPermission;
import lombok.Builder;

@Builder
public record ProjectMemberResponse(
        Long id,
        Long projectId,
        Long userId,
        String userName,
        String userEmail,
        ProjectPermission permission
) {
}
