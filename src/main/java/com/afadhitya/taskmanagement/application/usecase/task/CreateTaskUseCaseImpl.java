package com.afadhitya.taskmanagement.application.usecase.task;

import com.afadhitya.taskmanagement.application.dto.request.CreateTaskRequest;
import com.afadhitya.taskmanagement.application.dto.response.TaskResponse;
import com.afadhitya.taskmanagement.application.mapper.TaskMapper;
import com.afadhitya.taskmanagement.application.port.in.task.CreateTaskUseCase;
import com.afadhitya.taskmanagement.application.port.out.project.ProjectPersistencePort;
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
public class CreateTaskUseCaseImpl implements CreateTaskUseCase {

    private final TaskPersistencePort taskPersistencePort;
    private final ProjectPersistencePort projectPersistencePort;
    private final UserPersistencePort userPersistencePort;
    private final TaskMapper taskMapper;

    @Override
    public TaskResponse createTask(CreateTaskRequest request, Long createdByUserId) {
        Project project = projectPersistencePort.findById(request.projectId())
                .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + request.projectId()));

        User createdBy = userPersistencePort.findById(createdByUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + createdByUserId));

        Task.TaskBuilder taskBuilder = Task.builder()
                .title(request.title())
                .description(request.description())
                .status(request.status() != null ? request.status() : TaskStatus.TODO)
                .priority(request.priority() != null ? request.priority() : TaskPriority.MEDIUM)
                .dueDate(request.dueDate())
                .project(project)
                .createdBy(createdBy)
                .assigneeIds(request.assigneeIds() != null ? request.assigneeIds() : new HashSet<>());

        if (request.parentTaskId() != null) {
            Task parentTask = taskPersistencePort.findById(request.parentTaskId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent task not found with id: " + request.parentTaskId()));
            taskBuilder.parentTask(parentTask);
        }

        Task task = taskBuilder.build();
        Task savedTask = taskPersistencePort.save(task);

        return taskMapper.toResponse(savedTask);
    }
}
