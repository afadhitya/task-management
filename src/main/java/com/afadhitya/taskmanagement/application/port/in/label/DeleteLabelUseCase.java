package com.afadhitya.taskmanagement.application.port.in.label;

public interface DeleteLabelUseCase {

    void deleteLabel(Long labelId, Long currentUserId);
}
