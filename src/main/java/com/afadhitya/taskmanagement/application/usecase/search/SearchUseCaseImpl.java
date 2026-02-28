package com.afadhitya.taskmanagement.application.usecase.search;

import com.afadhitya.taskmanagement.application.dto.response.ProjectResponse;
import com.afadhitya.taskmanagement.application.dto.response.SearchResponse;
import com.afadhitya.taskmanagement.application.dto.response.TaskResponse;
import com.afadhitya.taskmanagement.application.dto.response.UserResponse;
import com.afadhitya.taskmanagement.application.mapper.ProjectMapper;
import com.afadhitya.taskmanagement.application.mapper.TaskMapper;
import com.afadhitya.taskmanagement.application.mapper.UserMapper;
import com.afadhitya.taskmanagement.application.port.in.search.SearchUseCase;
import com.afadhitya.taskmanagement.application.port.out.project.ProjectPersistencePort;
import com.afadhitya.taskmanagement.application.port.out.task.TaskPersistencePort;
import com.afadhitya.taskmanagement.application.port.out.user.UserPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.Project;
import com.afadhitya.taskmanagement.domain.entity.Task;
import com.afadhitya.taskmanagement.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchUseCaseImpl implements SearchUseCase {

    private final TaskPersistencePort taskPersistencePort;
    private final ProjectPersistencePort projectPersistencePort;
    private final UserPersistencePort userPersistencePort;
    private final TaskMapper taskMapper;
    private final ProjectMapper projectMapper;
    private final UserMapper userMapper;

    @Override
    public SearchResponse search(Long workspaceId, String query, String type) {
        List<TaskResponse> tasks = Collections.emptyList();
        List<ProjectResponse> projects = Collections.emptyList();
        List<UserResponse> users = Collections.emptyList();

        // If type is specified, search only that type
        // If type is null/empty, search all types
        if (type == null || type.isBlank() || "tasks".equalsIgnoreCase(type)) {
            List<Task> taskEntities = taskPersistencePort.searchByWorkspaceId(workspaceId, query);
            tasks = taskEntities.stream()
                    .map(taskMapper::toResponse)
                    .toList();
        }

        if (type == null || type.isBlank() || "projects".equalsIgnoreCase(type)) {
            List<Project> projectEntities = projectPersistencePort.searchByWorkspaceId(workspaceId, query);
            projects = projectEntities.stream()
                    .map(projectMapper::toResponse)
                    .toList();
        }

        if (type == null || type.isBlank() || "users".equalsIgnoreCase(type)) {
            List<User> userEntities = userPersistencePort.searchByWorkspaceId(workspaceId, query);
            users = userEntities.stream()
                    .map(userMapper::toResponse)
                    .toList();
        }

        return SearchResponse.builder()
                .tasks(tasks)
                .projects(projects)
                .users(users)
                .build();
    }
}
