package com.afadhitya.taskmanagement.application.port.out.project;

import com.afadhitya.taskmanagement.domain.entity.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectPersistencePort {

    Project save(Project project);

    Optional<Project> findById(Long id);

    List<Project> findByWorkspaceId(Long workspaceId);

    boolean existsById(Long id);

    void deleteById(Long id);
}
