package com.afadhitya.taskmanagement.application.port.in.task;

import com.afadhitya.taskmanagement.application.dto.request.CreateTaskRequest;
import com.afadhitya.taskmanagement.application.dto.response.TaskResponse;

public interface CreateTaskUseCase {

    TaskResponse createTask(CreateTaskRequest request, Long createdByUserId);
}
