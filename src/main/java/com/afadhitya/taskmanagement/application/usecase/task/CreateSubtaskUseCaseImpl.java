package com.afadhitya.taskmanagement.application.usecase.task;

import com.afadhitya.taskmanagement.application.dto.request.CreateSubtaskRequest;
import com.afadhitya.taskmanagement.application.dto.response.TaskResponse;
import com.afadhitya.taskmanagement.application.mapper.TaskMapper;
import com.afadhitya.taskmanagement.application.port.in.task.CreateSubtaskUseCase;
import com.afadhitya.taskmanagement.application.port.out.task.TaskPersistencePort;
import com.afadhitya.taskmanagement.application.port.out.user.UserPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.Project;
import com.afadhitya.taskmanagement.domain.entity.Task;
import com.afadhitya.taskmanagement.domain.entity.User;
import com.afadhitya.taskmanagement.domain.enums.TaskPriority;
import com.afadhitya.taskmanagement.domain.enums.TaskStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateSubtaskUseCaseImpl implements CreateSubtaskUseCase {

    private final TaskPersistencePort taskPersistencePort;
    private final UserPersistencePort userPersistencePort;
    private final TaskMapper taskMapper;

    @Override
    public TaskResponse createSubtask(Long parentTaskId, CreateSubtaskRequest request, Long createdByUserId) {
        Task parentTask = taskPersistencePort.findById(parentTaskId)
                .orElseThrow(() -> new IllegalArgumentException("Parent task not found with id: " + parentTaskId));

        User createdBy = userPersistencePort.findById(createdByUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + createdByUserId));

        Project project = parentTask.getProject();

        Task subtask = Task.builder()
                .title(request.title())
                .description(request.description())
                .status(request.status() != null ? request.status() : TaskStatus.TODO)
                .priority(request.priority() != null ? request.priority() : TaskPriority.MEDIUM)
                .dueDate(request.dueDate())
                .project(project)
                .parentTask(parentTask)
                .createdBy(createdBy)
                .assigneeIds(request.assigneeIds() != null ? request.assigneeIds() : new HashSet<>())
                .build();

        Task savedSubtask = taskPersistencePort.save(subtask);

        return taskMapper.toResponse(savedSubtask);
    }
}
