package com.afadhitya.taskmanagement.application.port.in.task;

import com.afadhitya.taskmanagement.application.dto.response.TaskResponse;

import java.util.List;

public interface GetMyTasksUseCase {

    List<TaskResponse> getMyTasks(Long userId);
}
