package com.afadhitya.taskmanagement.application.dto.request;

import com.afadhitya.taskmanagement.domain.enums.TaskPriority;
import com.afadhitya.taskmanagement.domain.enums.TaskStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Set;

@Builder
public record BulkUpdateTasksRequest(
        @NotEmpty(message = "Task IDs are required")
        Set<Long> taskIds,

        TaskStatus status,

        TaskPriority priority,

        Set<Long> assigneeIds,

        Boolean replaceAssignees
) {
}
