package com.afadhitya.taskmanagement.application.dto.response;

import lombok.Builder;

@Builder
public record LabelSummaryResponse(
        Long id,
        String name,
        String color
) {
}
