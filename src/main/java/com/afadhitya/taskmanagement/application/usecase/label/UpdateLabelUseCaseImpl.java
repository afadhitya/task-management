package com.afadhitya.taskmanagement.application.usecase.label;

import com.afadhitya.taskmanagement.application.dto.request.UpdateLabelRequest;
import com.afadhitya.taskmanagement.application.dto.response.LabelResponse;
import com.afadhitya.taskmanagement.application.mapper.LabelMapper;
import com.afadhitya.taskmanagement.application.port.in.label.UpdateLabelUseCase;
import com.afadhitya.taskmanagement.application.port.out.label.LabelPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.Label;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateLabelUseCaseImpl implements UpdateLabelUseCase {

    private final LabelPersistencePort labelPersistencePort;
    private final LabelMapper labelMapper;

    @Override
    @CacheEvict(value = "labels", allEntries = true)
    public LabelResponse updateLabel(Long labelId, UpdateLabelRequest request, Long currentUserId) {
        Label label = labelPersistencePort.findById(labelId)
                .orElseThrow(() -> new IllegalArgumentException("Label not found with id: " + labelId));

        labelMapper.updateEntityFromRequest(request, label);

        Label updatedLabel = labelPersistencePort.save(label);
        return labelMapper.toResponse(updatedLabel);
    }
}
