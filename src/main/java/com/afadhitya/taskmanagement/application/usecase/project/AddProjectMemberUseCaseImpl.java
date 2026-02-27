package com.afadhitya.taskmanagement.application.usecase.project;

import com.afadhitya.taskmanagement.application.dto.request.AddProjectMemberRequest;
import com.afadhitya.taskmanagement.application.dto.response.ProjectMemberResponse;
import com.afadhitya.taskmanagement.application.mapper.ProjectMemberMapper;
import com.afadhitya.taskmanagement.application.port.in.project.AddProjectMemberUseCase;
import com.afadhitya.taskmanagement.application.port.out.project.ProjectMemberPersistencePort;
import com.afadhitya.taskmanagement.application.port.out.project.ProjectPersistencePort;
import com.afadhitya.taskmanagement.application.port.out.user.UserPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.Project;
import com.afadhitya.taskmanagement.domain.entity.ProjectMember;
import com.afadhitya.taskmanagement.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AddProjectMemberUseCaseImpl implements AddProjectMemberUseCase {

    private final ProjectMemberPersistencePort projectMemberPersistencePort;
    private final ProjectPersistencePort projectPersistencePort;
    private final UserPersistencePort userPersistencePort;
    private final ProjectMemberMapper projectMemberMapper;

    @Override
    public ProjectMemberResponse addMember(Long projectId, AddProjectMemberRequest request) {
        if (projectMemberPersistencePort.existsByProjectIdAndUserId(projectId, request.userId())) {
            throw new IllegalArgumentException("User is already a member of this project");
        }

        Project project = projectPersistencePort.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + projectId));

        User user = userPersistencePort.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + request.userId()));

        ProjectMember projectMember = ProjectMember.builder()
                .project(project)
                .user(user)
                .permission(request.permission())
                .build();

        ProjectMember savedMember = projectMemberPersistencePort.save(projectMember);
        return projectMemberMapper.toResponse(savedMember);
    }
}
