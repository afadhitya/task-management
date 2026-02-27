package com.afadhitya.taskmanagement.application.dto.request;

import com.afadhitya.taskmanagement.domain.enums.TaskPriority;
import com.afadhitya.taskmanagement.domain.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.With;

import java.time.LocalDate;
import java.util.Set;

@Builder
public record CreateTaskRequest(
        @NotBlank(message = "Title is required")
        String title,

        String description,

        TaskStatus status,

        TaskPriority priority,

        LocalDate dueDate,

        @NotNull(message = "Project ID is required")
        @With
        Long projectId,

        Long parentTaskId,

        Set<Long> assigneeIds
) {
}
