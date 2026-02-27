package com.afadhitya.taskmanagement.application.dto.response;

import com.afadhitya.taskmanagement.domain.enums.JobStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record BulkJobResponse(
        String id,
        JobStatus status,
        Integer totalItems,
        Integer processedItems,
        Integer failedItems,
        Double progressPercentage,
        String jobType,
        String errorMessage,
        Long createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime completedAt
) {
}
