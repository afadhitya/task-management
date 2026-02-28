package com.afadhitya.taskmanagement.application.usecase.label;

import com.afadhitya.taskmanagement.application.port.in.label.RemoveLabelFromTaskUseCase;
import com.afadhitya.taskmanagement.application.port.out.task.TaskLabelPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RemoveLabelFromTaskUseCaseImpl implements RemoveLabelFromTaskUseCase {

    private final TaskLabelPersistencePort taskLabelPersistencePort;

    @Override
    public void removeLabelFromTask(Long taskId, Long labelId) {
        taskLabelPersistencePort.deleteByTaskIdAndLabelId(taskId, labelId);
    }
}
