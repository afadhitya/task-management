package com.afadhitya.taskmanagement.application.port.in.label;

import com.afadhitya.taskmanagement.application.dto.response.LabelResponse;

public interface AssignLabelToTaskUseCase {

    LabelResponse assignLabelToTask(Long taskId, Long labelId, Long currentUserId);
}
