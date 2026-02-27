package com.afadhitya.taskmanagement.application.port.in.task;

import com.afadhitya.taskmanagement.application.dto.request.CreateSubtaskRequest;
import com.afadhitya.taskmanagement.application.dto.response.TaskResponse;

public interface CreateSubtaskUseCase {

    TaskResponse createSubtask(Long parentTaskId, CreateSubtaskRequest request, Long createdByUserId);
}
