package com.afadhitya.taskmanagement.adapter.out.persistence.project;

import com.afadhitya.taskmanagement.adapter.out.persistence.ProjectMemberRepository;
import com.afadhitya.taskmanagement.application.port.out.project.ProjectMemberPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.ProjectMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProjectMemberPersistenceAdapter implements ProjectMemberPersistencePort {

    private final ProjectMemberRepository projectMemberRepository;

    @Override
    public ProjectMember save(ProjectMember projectMember) {
        return projectMemberRepository.save(projectMember);
    }

    @Override
    public Optional<ProjectMember> findByProjectIdAndUserId(Long projectId, Long userId) {
        return projectMemberRepository.findByProjectIdAndUserId(projectId, userId);
    }

    @Override
    public boolean existsByProjectIdAndUserId(Long projectId, Long userId) {
        return projectMemberRepository.existsByProjectIdAndUserId(projectId, userId);
    }

    @Override
    public void deleteByProjectIdAndUserId(Long projectId, Long userId) {
        projectMemberRepository.deleteByProjectIdAndUserId(projectId, userId);
    }
}
