package com.afadhitya.taskmanagement.application.dto.response.admin;

import lombok.Builder;

@Builder
public record PlanSummaryResponse(
    Long id,
    String planTier,
    String name,
    String description,
    boolean isActive,
    boolean isDefault,
    int featureCount,
    int limitCount
) {}
