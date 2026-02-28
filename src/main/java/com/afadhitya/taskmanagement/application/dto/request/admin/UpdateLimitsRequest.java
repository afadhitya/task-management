package com.afadhitya.taskmanagement.application.dto.request.admin;

import lombok.Builder;

import java.util.List;

@Builder
public record UpdateLimitsRequest(
    List<LimitValueRequest> limits
) {
    @Builder
    public record LimitValueRequest(
        String type,
        int value
    ) {}
}
