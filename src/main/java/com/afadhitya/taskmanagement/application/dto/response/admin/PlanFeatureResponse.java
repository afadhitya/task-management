package com.afadhitya.taskmanagement.application.dto.response.admin;

import lombok.Builder;

@Builder
public record PlanFeatureResponse(
    String code,
    String name,
    String category,
    boolean isEnabled
) {}
