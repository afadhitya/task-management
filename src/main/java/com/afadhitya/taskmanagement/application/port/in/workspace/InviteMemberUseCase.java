package com.afadhitya.taskmanagement.application.port.in.workspace;

import com.afadhitya.taskmanagement.application.dto.request.InviteMemberRequest;
import com.afadhitya.taskmanagement.application.dto.response.WorkspaceMemberResponse;

public interface InviteMemberUseCase {

    WorkspaceMemberResponse inviteMember(Long workspaceId, InviteMemberRequest request, Long currentUserId);
}
