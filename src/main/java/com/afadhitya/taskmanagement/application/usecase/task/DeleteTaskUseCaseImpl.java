package com.afadhitya.taskmanagement.application.usecase.task;

import com.afadhitya.taskmanagement.application.port.in.task.DeleteTaskUseCase;
import com.afadhitya.taskmanagement.application.port.out.task.TaskPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteTaskUseCaseImpl implements DeleteTaskUseCase {

    private final TaskPersistencePort taskPersistencePort;

    @Override
    public void deleteTask(Long id) {
        if (!taskPersistencePort.existsById(id)) {
            throw new IllegalArgumentException("Task not found with id: " + id);
        }
        taskPersistencePort.deleteById(id);
    }
}
