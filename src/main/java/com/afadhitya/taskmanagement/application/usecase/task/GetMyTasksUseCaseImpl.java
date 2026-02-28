package com.afadhitya.taskmanagement.application.usecase.task;

import com.afadhitya.taskmanagement.application.dto.response.TaskResponse;
import com.afadhitya.taskmanagement.application.mapper.TaskMapper;
import com.afadhitya.taskmanagement.application.port.in.task.GetMyTasksUseCase;
import com.afadhitya.taskmanagement.application.port.out.task.TaskPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetMyTasksUseCaseImpl implements GetMyTasksUseCase {

    private final TaskPersistencePort taskPersistencePort;
    private final TaskMapper taskMapper;

    @Override
    public List<TaskResponse> getMyTasks(Long userId) {
        List<Task> tasks = taskPersistencePort.findByAssigneeId(userId);
        return tasks.stream()
                .map(taskMapper::toResponse)
                .toList();
    }
}
