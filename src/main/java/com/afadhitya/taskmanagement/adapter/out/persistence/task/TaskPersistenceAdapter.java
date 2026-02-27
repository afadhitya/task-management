package com.afadhitya.taskmanagement.adapter.out.persistence.task;

import com.afadhitya.taskmanagement.adapter.out.persistence.TaskRepository;
import com.afadhitya.taskmanagement.application.dto.request.TaskFilterRequest;
import com.afadhitya.taskmanagement.application.port.out.task.TaskPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TaskPersistenceAdapter implements TaskPersistencePort {

    private final TaskRepository taskRepository;

    @Override
    public Task save(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    @Override
    public List<Task> findByProjectId(Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    @Override
    public Page<Task> findByProjectIdWithFilters(Long projectId, TaskFilterRequest filter, Pageable pageable) {
        return taskRepository.findByProjectIdWithFilters(
                projectId,
                filter.status(),
                filter.priority(),
                filter.parentTaskId(),
                filter.dueDateFrom(),
                filter.dueDateTo(),
                filter.search(),
                filter.assigneeIds(),
                pageable
        );
    }

    @Override
    public List<Task> findByParentTaskId(Long parentTaskId) {
        return taskRepository.findByParentTaskId(parentTaskId);
    }

    @Override
    public boolean existsById(Long id) {
        return taskRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        taskRepository.deleteById(id);
    }
}
