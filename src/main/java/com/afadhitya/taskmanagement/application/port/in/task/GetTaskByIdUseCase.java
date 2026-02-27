package com.afadhitya.taskmanagement.application.port.in.task;

import com.afadhitya.taskmanagement.application.dto.response.TaskResponse;

public interface GetTaskByIdUseCase {

    TaskResponse getTaskById(Long id);
}
