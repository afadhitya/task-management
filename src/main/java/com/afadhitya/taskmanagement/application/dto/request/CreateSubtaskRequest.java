package com.afadhitya.taskmanagement.application.dto.request;

import com.afadhitya.taskmanagement.domain.enums.TaskPriority;
import com.afadhitya.taskmanagement.domain.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Set;

@Builder
public record CreateSubtaskRequest(
        @NotBlank(message = "Title is required")
        String title,

        String description,

        TaskStatus status,

        TaskPriority priority,

        LocalDate dueDate,

        Set<Long> assigneeIds
) {
}
