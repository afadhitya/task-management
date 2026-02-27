package com.afadhitya.taskmanagement.application.usecase.task;

import com.afadhitya.taskmanagement.application.dto.request.UpdateTaskRequest;
import com.afadhitya.taskmanagement.application.dto.response.TaskResponse;
import com.afadhitya.taskmanagement.application.mapper.TaskMapper;
import com.afadhitya.taskmanagement.application.port.in.task.UpdateTaskUseCase;
import com.afadhitya.taskmanagement.application.port.out.task.TaskPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateTaskUseCaseImpl implements UpdateTaskUseCase {

    private final TaskPersistencePort taskPersistencePort;
    private final TaskMapper taskMapper;

    @Override
    public TaskResponse updateTask(Long id, UpdateTaskRequest request) {
        Task task = taskPersistencePort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id));

        taskMapper.updateEntityFromRequest(request, task);

        Task updatedTask = taskPersistencePort.save(task);
        return taskMapper.toResponse(updatedTask);
    }
}
