package com.afadhitya.taskmanagement.adapter.out.persistence.label;

import com.afadhitya.taskmanagement.adapter.out.persistence.LabelRepository;
import com.afadhitya.taskmanagement.application.port.out.label.LabelPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.Label;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LabelPersistenceAdapter implements LabelPersistencePort {

    private final LabelRepository labelRepository;

    @Override
    public Label save(Label label) {
        return labelRepository.save(label);
    }

    @Override
    public Optional<Label> findById(Long id) {
        return labelRepository.findById(id);
    }

    @Override
    public List<Label> findByWorkspaceId(Long workspaceId) {
        return labelRepository.findByWorkspaceId(workspaceId);
    }

    @Override
    public List<Label> findByProjectId(Long projectId) {
        return labelRepository.findByProjectId(projectId);
    }

    @Override
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
    public void deleteById(Long id) {
        labelRepository.deleteById(id);
    }
}
