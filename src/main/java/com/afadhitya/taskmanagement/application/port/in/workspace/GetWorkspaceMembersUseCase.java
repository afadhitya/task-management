package com.afadhitya.taskmanagement.application.port.in.workspace;

import com.afadhitya.taskmanagement.application.dto.response.WorkspaceMemberResponse;

import java.util.List;

public interface GetWorkspaceMembersUseCase {

    List<WorkspaceMemberResponse> getWorkspaceMembers(Long workspaceId, Long currentUserId);
}
