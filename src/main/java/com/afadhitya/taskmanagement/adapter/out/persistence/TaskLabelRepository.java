package com.afadhitya.taskmanagement.adapter.out.persistence;

import com.afadhitya.taskmanagement.domain.entity.TaskLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskLabelRepository extends JpaRepository<TaskLabel, Long> {

    boolean existsByTaskIdAndLabelId(Long taskId, Long labelId);

    Optional<TaskLabel> findByTaskIdAndLabelId(Long taskId, Long labelId);

    void deleteByTaskIdAndLabelId(Long taskId, Long labelId);
}
