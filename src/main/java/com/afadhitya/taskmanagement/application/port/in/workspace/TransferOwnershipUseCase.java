package com.afadhitya.taskmanagement.application.port.in.workspace;

import com.afadhitya.taskmanagement.application.dto.request.TransferOwnershipRequest;
import com.afadhitya.taskmanagement.application.dto.response.WorkspaceMemberResponse;

public interface TransferOwnershipUseCase {

    WorkspaceMemberResponse transferOwnership(Long workspaceId, TransferOwnershipRequest request, Long currentUserId);
}
