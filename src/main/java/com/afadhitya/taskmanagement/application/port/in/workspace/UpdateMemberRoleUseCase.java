package com.afadhitya.taskmanagement.application.port.in.workspace;

import com.afadhitya.taskmanagement.application.dto.request.UpdateMemberRoleRequest;
import com.afadhitya.taskmanagement.application.dto.response.WorkspaceMemberResponse;

public interface UpdateMemberRoleUseCase {

    WorkspaceMemberResponse updateMemberRole(Long workspaceId, Long userId, UpdateMemberRoleRequest request, Long currentUserId);
}
