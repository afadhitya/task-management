package com.afadhitya.taskmanagement.application.port.out.label;

import com.afadhitya.taskmanagement.domain.entity.Label;

import java.util.List;
import java.util.Optional;

public interface LabelPersistencePort {

    Label save(Label label);

    Optional<Label> findById(Long id);

    List<Label> findByWorkspaceId(Long workspaceId);

    List<Label> findByProjectId(Long projectId);

    List<Label> findByWorkspaceIdAndProjectIdIsNull(Long workspaceId);

    boolean existsByWorkspaceIdAndNameAndProjectIdIsNull(Long workspaceId, String name);

    boolean existsByProjectIdAndName(Long projectId, String name);

    void deleteById(Long id);
}
