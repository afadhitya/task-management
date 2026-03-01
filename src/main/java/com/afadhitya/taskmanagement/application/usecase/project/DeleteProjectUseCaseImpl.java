package com.afadhitya.taskmanagement.application.usecase.project;

import com.afadhitya.taskmanagement.application.port.in.project.DeleteProjectUseCase;
import com.afadhitya.taskmanagement.application.port.out.project.ProjectPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteProjectUseCaseImpl implements DeleteProjectUseCase {

    private final ProjectPersistencePort projectPersistencePort;

    @Override
    @CacheEvict(value = "projects", key = "#id")
    public void deleteProject(Long id) {
        if (!projectPersistencePort.existsById(id)) {
            throw new IllegalArgumentException("Project not found with id: " + id);
        }
        projectPersistencePort.deleteById(id);
    }
}
