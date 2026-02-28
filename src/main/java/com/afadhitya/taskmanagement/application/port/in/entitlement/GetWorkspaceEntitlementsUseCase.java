package com.afadhitya.taskmanagement.application.port.in.entitlement;

import com.afadhitya.taskmanagement.application.dto.response.WorkspaceEntitlementResponse;

public interface GetWorkspaceEntitlementsUseCase {
    WorkspaceEntitlementResponse getEntitlements(Long workspaceId);
}
