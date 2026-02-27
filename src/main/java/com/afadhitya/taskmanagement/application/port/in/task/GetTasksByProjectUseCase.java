package com.afadhitya.taskmanagement.application.port.in.task;

import com.afadhitya.taskmanagement.application.dto.request.TaskFilterRequest;
import com.afadhitya.taskmanagement.application.dto.response.PagedResponse;
import com.afadhitya.taskmanagement.application.dto.response.TaskResponse;

public interface GetTasksByProjectUseCase {

    PagedResponse<TaskResponse> getTasksByProject(Long projectId, TaskFilterRequest filter, int page, int size, String sortBy, String sortDirection);
}
