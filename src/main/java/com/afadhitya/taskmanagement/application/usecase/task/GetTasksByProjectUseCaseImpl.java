package com.afadhitya.taskmanagement.application.usecase.task;

import com.afadhitya.taskmanagement.application.dto.request.TaskFilterRequest;
import com.afadhitya.taskmanagement.application.dto.response.PagedResponse;
import com.afadhitya.taskmanagement.application.dto.response.TaskResponse;
import com.afadhitya.taskmanagement.application.mapper.TaskMapper;
import com.afadhitya.taskmanagement.application.port.in.task.GetTasksByProjectUseCase;
import com.afadhitya.taskmanagement.application.port.out.task.TaskPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetTasksByProjectUseCaseImpl implements GetTasksByProjectUseCase {

    private final TaskPersistencePort taskPersistencePort;
    private final TaskMapper taskMapper;

    @Override
    public PagedResponse<TaskResponse> getTasksByProject(Long projectId, TaskFilterRequest filter, int page, int size, String sortBy, String sortDirection) {
        Sort sort = Sort.by("asc".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortBy != null ? sortBy : "createdAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Task> taskPage = taskPersistencePort.findByProjectIdWithFilters(projectId, filter, pageable);

        List<TaskResponse> content = taskPage.getContent().stream()
                .map(taskMapper::toResponse)
                .toList();

        return PagedResponse.<TaskResponse>builder()
                .content(content)
                .page(taskPage.getNumber())
                .size(taskPage.getSize())
                .totalElements(taskPage.getTotalElements())
                .totalPages(taskPage.getTotalPages())
                .first(taskPage.isFirst())
                .last(taskPage.isLast())
                .build();
    }
}
