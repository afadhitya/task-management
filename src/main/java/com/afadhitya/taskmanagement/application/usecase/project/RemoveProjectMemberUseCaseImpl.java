package com.afadhitya.taskmanagement.application.usecase.project;

import com.afadhitya.taskmanagement.application.port.in.project.RemoveProjectMemberUseCase;
import com.afadhitya.taskmanagement.application.port.out.project.ProjectMemberPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RemoveProjectMemberUseCaseImpl implements RemoveProjectMemberUseCase {

    private final ProjectMemberPersistencePort projectMemberPersistencePort;

    @Override
    @CacheEvict(value = "projectMembers", key = "#projectId")
    public void removeMember(Long projectId, Long userId) {
        if (!projectMemberPersistencePort.existsByProjectIdAndUserId(projectId, userId)) {
            throw new IllegalArgumentException("User is not a member of this project");
        }
        projectMemberPersistencePort.deleteByProjectIdAndUserId(projectId, userId);
    }
}
