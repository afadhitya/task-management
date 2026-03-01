package com.afadhitya.taskmanagement.adapter.out.persistence.project;

import com.afadhitya.taskmanagement.adapter.out.persistence.ProjectMemberRepository;
import com.afadhitya.taskmanagement.application.port.out.project.ProjectMemberPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.ProjectMember;
import com.afadhitya.taskmanagement.domain.enums.ProjectPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProjectMemberPersistenceAdapter implements ProjectMemberPersistencePort {

    private final ProjectMemberRepository projectMemberRepository;

    @Override
    @CacheEvict(value = "projectMembers", key = "#projectMember.project.id")
    public ProjectMember save(ProjectMember projectMember) {
        return projectMemberRepository.save(projectMember);
    }

    @Override
    public Optional<ProjectMember> findByProjectIdAndUserId(Long projectId, Long userId) {
        return projectMemberRepository.findByProjectIdAndUserId(projectId, userId);
    }

    @Override
    @Cacheable(value = "projectMembers", key = "#projectId")
    public List<ProjectMember> findByProjectId(Long projectId) {
        return projectMemberRepository.findByProjectId(projectId);
    }

    @Override
    public boolean existsByProjectIdAndUserId(Long projectId, Long userId) {
        return projectMemberRepository.existsByProjectIdAndUserId(projectId, userId);
    }

    @Override
    @CacheEvict(value = "projectMembers", key = "#projectId")
    public void deleteByProjectIdAndUserId(Long projectId, Long userId) {
        projectMemberRepository.deleteByProjectIdAndUserId(projectId, userId);
    }

    @Override
    public long countByProjectIdAndPermission(Long projectId, ProjectPermission permission) {
        return projectMemberRepository.countByProjectIdAndPermission(projectId, permission);
    }
}
