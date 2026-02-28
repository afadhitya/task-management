package com.afadhitya.taskmanagement.application.usecase.label;

import com.afadhitya.taskmanagement.application.port.in.label.DeleteLabelUseCase;
import com.afadhitya.taskmanagement.application.port.out.label.LabelPersistencePort;
import com.afadhitya.taskmanagement.application.service.AuditEventPublisher;
import com.afadhitya.taskmanagement.domain.entity.Label;
import com.afadhitya.taskmanagement.domain.enums.AuditEntityType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteLabelUseCaseImpl implements DeleteLabelUseCase {

    private final LabelPersistencePort labelPersistencePort;
    private final AuditEventPublisher auditEventPublisher;

    @Override
    public void deleteLabel(Long labelId, Long currentUserId) {
        Label label = labelPersistencePort.findById(labelId)
                .orElseThrow(() -> new IllegalArgumentException("Label not found with id: " + labelId));

        Long workspaceId = label.getWorkspace().getId();

        auditEventPublisher.publishDelete(
                workspaceId,
                currentUserId,
                AuditEntityType.LABEL,
                labelId,
                Map.of("name", label.getName(), "color", label.getColor())
        );

        labelPersistencePort.deleteById(labelId);
    }
}
