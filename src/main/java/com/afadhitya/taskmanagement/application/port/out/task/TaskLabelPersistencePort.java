package com.afadhitya.taskmanagement.application.port.out.task;

import com.afadhitya.taskmanagement.domain.entity.TaskLabel;

public interface TaskLabelPersistencePort {

    TaskLabel save(TaskLabel taskLabel);

    boolean existsByTaskIdAndLabelId(Long taskId, Long labelId);

    void deleteByTaskIdAndLabelId(Long taskId, Long labelId);
}
