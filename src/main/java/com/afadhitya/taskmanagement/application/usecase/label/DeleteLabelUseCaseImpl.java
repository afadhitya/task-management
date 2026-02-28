package com.afadhitya.taskmanagement.application.usecase.label;

import com.afadhitya.taskmanagement.application.port.in.label.DeleteLabelUseCase;
import com.afadhitya.taskmanagement.application.port.out.label.LabelPersistencePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteLabelUseCaseImpl implements DeleteLabelUseCase {

    private final LabelPersistencePort labelPersistencePort;

    @Override
    public void deleteLabel(Long labelId, Long currentUserId) {
        labelPersistencePort.deleteById(labelId);
    }
}
