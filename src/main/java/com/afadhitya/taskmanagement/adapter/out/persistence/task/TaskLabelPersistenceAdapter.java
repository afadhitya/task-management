package com.afadhitya.taskmanagement.adapter.out.persistence.task;

import com.afadhitya.taskmanagement.adapter.out.persistence.TaskLabelRepository;
import com.afadhitya.taskmanagement.application.port.out.task.TaskLabelPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.TaskLabel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskLabelPersistenceAdapter implements TaskLabelPersistencePort {

    private final TaskLabelRepository taskLabelRepository;

    @Override
    public TaskLabel save(TaskLabel taskLabel) {
        return taskLabelRepository.save(taskLabel);
    }

    @Override
    public boolean existsByTaskIdAndLabelId(Long taskId, Long labelId) {
        return taskLabelRepository.existsByTaskIdAndLabelId(taskId, labelId);
    }

    @Override
    public void deleteByTaskIdAndLabelId(Long taskId, Long labelId) {
        taskLabelRepository.deleteByTaskIdAndLabelId(taskId, labelId);
    }
}
