package com.afadhitya.taskmanagement.adapter.out.persistence.label;

import com.afadhitya.taskmanagement.adapter.out.persistence.LabelRepository;
import com.afadhitya.taskmanagement.application.port.out.label.LabelPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.Label;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LabelPersistenceAdapter implements LabelPersistencePort {

    private final LabelRepository labelRepository;

    @Override
    @CacheEvict(value = "labels", allEntries = true)
    public Label save(Label label) {
        return labelRepository.save(label);
    }

    @Override
    @Cacheable(value = "labels", key = "'id:' + #id")
    public Optional<Label> findById(Long id) {
        return labelRepository.findById(id);
    }

    @Override
    @Cacheable(value = "labels", key = "'workspace:' + #workspaceId")
    public List<Label> findByWorkspaceId(Long workspaceId) {
        return labelRepository.findByWorkspaceId(workspaceId);
    }

    @Override
    @Cacheable(value = "labels", key = "'project:' + #projectId")
    public List<Label> findByProjectId(Long projectId) {
        return labelRepository.findByProjectId(projectId);
    }

    @Override
    @Cacheable(value = "labels", key = "'workspace-global:' + #workspaceId")
    public List<Label> findByWorkspaceIdAndProjectIdIsNull(Long workspaceId) {
        return labelRepository.findByWorkspaceIdAndProjectIdIsNull(workspaceId);
    }

    @Override
    public boolean existsByWorkspaceIdAndNameAndProjectIdIsNull(Long workspaceId, String name) {
        return labelRepository.existsByWorkspaceIdAndNameAndProjectIdIsNull(workspaceId, name);
    }

    @Override
    public boolean existsByProjectIdAndName(Long projectId, String name) {
        return labelRepository.existsByProjectIdAndName(projectId, name);
    }

    @Override
    @CacheEvict(value = "labels", allEntries = true)
    public void deleteById(Long id) {
        labelRepository.deleteById(id);
    }
}
