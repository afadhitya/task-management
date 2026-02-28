package com.afadhitya.taskmanagement.application.usecase.label;

import com.afadhitya.taskmanagement.application.dto.request.UpdateLabelRequest;
import com.afadhitya.taskmanagement.application.dto.response.LabelResponse;
import com.afadhitya.taskmanagement.application.mapper.LabelMapper;
import com.afadhitya.taskmanagement.application.port.in.label.UpdateLabelUseCase;
import com.afadhitya.taskmanagement.application.port.out.label.LabelPersistencePort;
import com.afadhitya.taskmanagement.application.service.AuditEventPublisher;
import com.afadhitya.taskmanagement.domain.entity.Label;
import com.afadhitya.taskmanagement.domain.enums.AuditEntityType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateLabelUseCaseImpl implements UpdateLabelUseCase {

    private final LabelPersistencePort labelPersistencePort;
    private final LabelMapper labelMapper;
    private final AuditEventPublisher auditEventPublisher;

    @Override
    public LabelResponse updateLabel(Long labelId, UpdateLabelRequest request, Long currentUserId) {
        Label label = labelPersistencePort.findById(labelId)
                .orElseThrow(() -> new IllegalArgumentException("Label not found with id: " + labelId));

        Map<String, Object> diff = new HashMap<>();
        if (request.name() != null && !request.name().equals(label.getName())) {
            diff.put("name", Map.of("old", label.getName(), "new", request.name()));
        }
        if (request.color() != null && !request.color().equals(label.getColor())) {
            diff.put("color", Map.of("old", label.getColor(), "new", request.color()));
        }

        labelMapper.updateEntityFromRequest(request, label);

        Label updatedLabel = labelPersistencePort.save(label);

        if (!diff.isEmpty()) {
            auditEventPublisher.publishUpdate(
                    updatedLabel.getWorkspace().getId(),
                    currentUserId,
                    AuditEntityType.LABEL,
                    labelId,
                    diff
            );
        }

        return labelMapper.toResponse(updatedLabel);
    }
}
