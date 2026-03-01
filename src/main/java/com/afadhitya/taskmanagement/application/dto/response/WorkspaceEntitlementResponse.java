package com.afadhitya.taskmanagement.application.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record WorkspaceEntitlementResponse(
    Long workspaceId,
    PlanInfo plan,
    List<FeatureInfo> features,
    List<LimitInfo> limits
) {
    @Builder
    public record PlanInfo(
        String tier,
        String name
    ) {}

    @Builder
    public record FeatureInfo(
        String code,
        String name,
        boolean isEnabled
    ) {}

    @Builder
    public record LimitInfo(
        String type,
        int limit,
        int used,
        int remaining
    ) {}
}
