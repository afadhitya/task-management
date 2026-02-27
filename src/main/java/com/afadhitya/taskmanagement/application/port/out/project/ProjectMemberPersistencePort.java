package com.afadhitya.taskmanagement.application.port.out.project;

import com.afadhitya.taskmanagement.domain.entity.ProjectMember;

import java.util.Optional;

public interface ProjectMemberPersistencePort {

    ProjectMember save(ProjectMember projectMember);

    Optional<ProjectMember> findByProjectIdAndUserId(Long projectId, Long userId);

    boolean existsByProjectIdAndUserId(Long projectId, Long userId);

    void deleteByProjectIdAndUserId(Long projectId, Long userId);
}
