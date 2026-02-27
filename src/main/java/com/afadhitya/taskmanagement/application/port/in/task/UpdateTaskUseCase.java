package com.afadhitya.taskmanagement.application.port.in.task;

import com.afadhitya.taskmanagement.application.dto.request.UpdateTaskRequest;
import com.afadhitya.taskmanagement.application.dto.response.TaskResponse;

public interface UpdateTaskUseCase {

    TaskResponse updateTask(Long id, UpdateTaskRequest request);
}
