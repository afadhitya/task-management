package com.afadhitya.taskmanagement.application.port.out.task;

import com.afadhitya.taskmanagement.domain.entity.Task;

import java.util.List;
import java.util.Optional;

public interface TaskPersistencePort {

    Task save(Task task);

    Optional<Task> findById(Long id);

    List<Task> findByProjectId(Long projectId);

    List<Task> findByParentTaskId(Long parentTaskId);

    boolean existsById(Long id);

    void deleteById(Long id);
}
