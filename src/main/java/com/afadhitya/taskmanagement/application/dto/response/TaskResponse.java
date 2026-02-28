package com.afadhitya.taskmanagement.application.dto.response;

import com.afadhitya.taskmanagement.domain.enums.TaskPriority;
import com.afadhitya.taskmanagement.domain.enums.TaskStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Builder
public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        LocalDate dueDate,
        Integer position,
        Long projectId,
        String projectName,
        Long parentTaskId,
        Set<Long> assigneeIds,
        List<LabelSummaryResponse> labels,
        Long createdBy,
        String createdByName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
