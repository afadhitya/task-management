package com.afadhitya.taskmanagement.application.dto.request;

import com.afadhitya.taskmanagement.domain.enums.ProjectPermission;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateProjectMemberRoleRequest(
        @NotNull(message = "Permission is required")
        ProjectPermission permission
) {
}
