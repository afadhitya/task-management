package com.afadhitya.taskmanagement.application.dto.request;

import com.afadhitya.taskmanagement.domain.enums.TaskPriority;
import com.afadhitya.taskmanagement.domain.enums.TaskStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Set;

@Builder
public record TaskFilterRequest(
        TaskStatus status,
        TaskPriority priority,
        Set<Long> assigneeIds,
        LocalDate dueDateFrom,
        LocalDate dueDateTo,
        Long parentTaskId,
        String search
) {
}
