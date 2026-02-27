package com.afadhitya.taskmanagement.application.dto.request;

import com.afadhitya.taskmanagement.domain.enums.TaskPriority;
import com.afadhitya.taskmanagement.domain.enums.TaskStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Set;

@Builder
public record UpdateTaskRequest(
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        LocalDate dueDate,
        Integer position,
        Set<Long> assigneeIds
) {
}
