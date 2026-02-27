package com.afadhitya.taskmanagement.adapter.out.persistence.workspace;

import com.afadhitya.taskmanagement.adapter.out.persistence.WorkspaceRepository;
import com.afadhitya.taskmanagement.application.port.out.workspace.WorkspacePersistencePort;
import com.afadhitya.taskmanagement.domain.entity.Workspace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WorkspacePersistenceAdapter implements WorkspacePersistencePort {

    private final WorkspaceRepository workspaceRepository;

    @Override
    public Workspace save(Workspace workspace) {
        return workspaceRepository.save(workspace);
    }

    @Override
    public Optional<Workspace> findById(Long id) {
        return workspaceRepository.findById(id);
    }

    @Override
    public List<Workspace> findAll() {
        return workspaceRepository.findAll();
    }

    @Override
    public boolean existsById(Long id) {
        return workspaceRepository.existsById(id);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return workspaceRepository.existsBySlug(slug);
    }

    @Override
    public void deleteById(Long id) {
        workspaceRepository.deleteById(id);
    }
}
