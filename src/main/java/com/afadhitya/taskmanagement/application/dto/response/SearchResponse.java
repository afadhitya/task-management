package com.afadhitya.taskmanagement.application.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record SearchResponse(
        List<TaskResponse> tasks,
        List<ProjectResponse> projects,
        List<UserResponse> users
) {
}
