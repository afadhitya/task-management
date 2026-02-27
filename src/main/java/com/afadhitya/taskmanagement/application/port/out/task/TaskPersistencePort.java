package com.afadhitya.taskmanagement.application.port.out.task;

import com.afadhitya.taskmanagement.application.dto.request.TaskFilterRequest;
import com.afadhitya.taskmanagement.domain.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TaskPersistencePort {

    Task save(Task task);

    Optional<Task> findById(Long id);

    List<Task> findByProjectId(Long projectId);

    Page<Task> findByProjectIdWithFilters(Long projectId, TaskFilterRequest filter, Pageable pageable);

    List<Task> findByParentTaskId(Long parentTaskId);

    boolean existsById(Long id);

    void deleteById(Long id);
}
