package com.afadhitya.taskmanagement.application.dto.response.admin;

import lombok.Builder;

import java.util.List;

@Builder
public record PlanDetailResponse(
    Long id,
    String planTier,
    String name,
    String description,
    boolean isActive,
    boolean isDefault,
    List<PlanFeatureResponse> features,
    List<PlanLimitResponse> limits
) {}
