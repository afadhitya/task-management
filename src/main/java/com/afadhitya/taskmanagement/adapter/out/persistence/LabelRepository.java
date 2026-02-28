package com.afadhitya.taskmanagement.adapter.out.persistence;

import com.afadhitya.taskmanagement.domain.entity.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {

    List<Label> findByWorkspaceId(Long workspaceId);

    List<Label> findByProjectId(Long projectId);

    List<Label> findByWorkspaceIdAndProjectIdIsNull(Long workspaceId);

    boolean existsByWorkspaceIdAndNameAndProjectIdIsNull(Long workspaceId, String name);

    boolean existsByProjectIdAndName(Long projectId, String name);
}
