package com.afadhitya.taskmanagement.application.dto.response;

import com.afadhitya.taskmanagement.domain.enums.PlanTier;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record WorkspaceResponse(
        Long id,
        String name,
        String slug,
        String logoUrl,
        PlanTier planTier,
        Long ownerId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
