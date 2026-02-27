package com.afadhitya.taskmanagement.application.usecase.project;

import com.afadhitya.taskmanagement.application.dto.response.ProjectMemberResponse;
import com.afadhitya.taskmanagement.application.mapper.ProjectMemberMapper;
import com.afadhitya.taskmanagement.application.port.in.project.GetProjectMembersUseCase;
import com.afadhitya.taskmanagement.application.port.out.project.ProjectMemberPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.ProjectMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetProjectMembersUseCaseImpl implements GetProjectMembersUseCase {

    private final ProjectMemberPersistencePort projectMemberPersistencePort;
    private final ProjectMemberMapper projectMemberMapper;

    @Override
    public List<ProjectMemberResponse> getProjectMembers(Long projectId) {
        List<ProjectMember> members = projectMemberPersistencePort.findByProjectId(projectId);
        return members.stream()
                .map(projectMemberMapper::toResponse)
                .toList();
    }
}
