package com.afadhitya.taskmanagement.application.dto.request.admin;

import lombok.Builder;

import java.util.List;

@Builder
public record UpdateFeaturesRequest(
    List<FeatureToggleRequest> features
) {
    @Builder
    public record FeatureToggleRequest(
        String code,
        boolean isEnabled
    ) {}
}
