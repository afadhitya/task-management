package com.afadhitya.taskmanagement.application.port.out.workspace;

import com.afadhitya.taskmanagement.domain.entity.Workspace;

import java.util.List;
import java.util.Optional;

public interface WorkspacePersistencePort {

    Workspace save(Workspace workspace);

    Optional<Workspace> findById(Long id);

    List<Workspace> findAll();

    boolean existsById(Long id);

    boolean existsBySlug(String slug);

    void deleteById(Long id);

    int countProjects(Long workspaceId);

    int countMembers(Long workspaceId);
}
