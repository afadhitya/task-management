package com.afadhitya.taskmanagement.application.dto.response.admin;

import lombok.Builder;

@Builder
public record PlanLimitResponse(
    String type,
    int value
) {}
