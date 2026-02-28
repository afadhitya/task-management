package com.afadhitya.taskmanagement.application.usecase.label;

import com.afadhitya.taskmanagement.application.port.in.label.DeleteLabelUseCase;
import com.afadhitya.taskmanagement.application.port.out.label.LabelPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.Label;
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
        Label label = labelPersistencePort.findById(labelId)
                .orElseThrow(() -> new IllegalArgumentException("Label not found with id: " + labelId));
        labelPersistencePort.deleteById(labelId);
    }
}
