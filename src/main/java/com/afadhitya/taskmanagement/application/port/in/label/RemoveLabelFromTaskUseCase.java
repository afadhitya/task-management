package com.afadhitya.taskmanagement.application.port.in.label;

public interface RemoveLabelFromTaskUseCase {

    void removeLabelFromTask(Long taskId, Long labelId);
}
