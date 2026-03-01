package com.afadhitya.taskmanagement.adapter.out.persistence.project;

import com.afadhitya.taskmanagement.adapter.out.persistence.ProjectRepository;
import com.afadhitya.taskmanagement.application.port.out.project.ProjectPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProjectPersistenceAdapter implements ProjectPersistencePort {

    private final ProjectRepository projectRepository;

    @Override
    @CacheEvict(value = "projects", key = "#project.id")
    public Project save(Project project) {
        return projectRepository.save(project);
    }

    @Override
    @Cacheable(value = "projects", key = "#id")
    public Optional<Project> findById(Long id) {
        return projectRepository.findById(id);
    }

    @Override
    public List<Project> findByWorkspaceId(Long workspaceId) {
        return projectRepository.findByWorkspaceId(workspaceId);
    }

    @Override
    public boolean existsById(Long id) {
        return projectRepository.existsById(id);
    }

    @Override
    @CacheEvict(value = "projects", key = "#id")
    public void deleteById(Long id) {
        projectRepository.deleteById(id);
    }

    @Override
    public List<Project> searchByWorkspaceId(Long workspaceId, String query) {
        return projectRepository.searchByWorkspaceId(workspaceId, query);
    }
}
