package com.afadhitya.taskmanagement.application.dto.response;

import lombok.Builder;

import java.time.Instant;
import java.util.Map;

@Builder
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        Map<String, Object> details,
        String path
) {
}
