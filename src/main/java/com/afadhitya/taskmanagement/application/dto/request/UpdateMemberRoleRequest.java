package com.afadhitya.taskmanagement.application.dto.request;

import com.afadhitya.taskmanagement.domain.enums.WorkspaceRole;
import jakarta.validation.constraints.NotNull;

public record UpdateMemberRoleRequest(
        @NotNull(message = "Role is required")
        WorkspaceRole role
) {
}
