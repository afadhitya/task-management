package com.afadhitya.taskmanagement.application.usecase.audit;

import com.afadhitya.taskmanagement.application.dto.request.CreateLabelRequest;
import com.afadhitya.taskmanagement.application.dto.request.UpdateLabelRequest;
import com.afadhitya.taskmanagement.application.dto.response.LabelResponse;
import com.afadhitya.taskmanagement.application.port.in.label.CreateLabelUseCase;
import com.afadhitya.taskmanagement.application.port.in.label.DeleteLabelUseCase;
import com.afadhitya.taskmanagement.application.port.in.label.UpdateLabelUseCase;
import com.afadhitya.taskmanagement.application.port.out.label.LabelPersistencePort;
import com.afadhitya.taskmanagement.application.service.AuditLogService;
import com.afadhitya.taskmanagement.application.usecase.feature.AuditFeatureInterceptor;
import com.afadhitya.taskmanagement.application.usecase.label.CreateLabelUseCaseImpl;
import com.afadhitya.taskmanagement.application.usecase.label.DeleteLabelUseCaseImpl;
import com.afadhitya.taskmanagement.application.usecase.label.UpdateLabelUseCaseImpl;
import com.afadhitya.taskmanagement.domain.entity.Label;
import com.afadhitya.taskmanagement.domain.enums.AuditEntityType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Primary
@RequiredArgsConstructor
public class AuditedLabelUseCases {

    private final LabelPersistencePort labelPersistencePort;
    private final AuditLogService auditLogService;
    private final AuditFeatureInterceptor auditInterceptor;

    @Service
    @Primary
    @RequiredArgsConstructor
    public class CreateLabel implements CreateLabelUseCase {

        private final CreateLabelUseCaseImpl delegate;

        @Override
        @Transactional
        public LabelResponse createLabel(Long workspaceId, CreateLabelRequest request, Long createdByUserId) {
            LabelResponse response = delegate.createLabel(workspaceId, request, createdByUserId);

            if (!auditInterceptor.shouldAudit(workspaceId)) {
                return response;
            }

            Map<String, Object> newValues = new HashMap<>();
            newValues.put("name", response.name());
            newValues.put("color", response.color());
            if (request.projectId() != null) {
                newValues.put("projectId", request.projectId());
            }

            auditInterceptor.auditCreate(
                    workspaceId,
                    createdByUserId,
                    AuditEntityType.LABEL,
                    response.id(),
                    newValues
            );

            return response;
        }
    }

    @Service
    @Primary
    @RequiredArgsConstructor
    public class UpdateLabel implements UpdateLabelUseCase {

        private final UpdateLabelUseCaseImpl delegate;

        @Override
        @Transactional
        public LabelResponse updateLabel(Long labelId, UpdateLabelRequest request, Long currentUserId) {
            Label label = labelPersistencePort.findById(labelId)
                    .orElseThrow(() -> new IllegalArgumentException("Label not found with id: " + labelId));

            Long workspaceId = label.getWorkspace().getId();

            boolean shouldAudit = auditInterceptor.shouldAudit(workspaceId);

            Map<String, Object> diff = new HashMap<>();
            if (shouldAudit) {
                if (request.name() != null && !request.name().equals(label.getName())) {
                    diff.put("name", Map.of("old", label.getName(), "new", request.name()));
                }
                if (request.color() != null && !request.color().equals(label.getColor())) {
                    diff.put("color", Map.of("old", label.getColor(), "new", request.color()));
                }
            }

            LabelResponse response = delegate.updateLabel(labelId, request, currentUserId);

            if (shouldAudit && !diff.isEmpty()) {
                auditInterceptor.auditUpdate(
                        workspaceId,
                        currentUserId,
                        AuditEntityType.LABEL,
                        labelId,
                        diff
                );
            }

            return response;
        }
    }

    @Service
    @Primary
    @RequiredArgsConstructor
    public class DeleteLabel implements DeleteLabelUseCase {

        private final DeleteLabelUseCaseImpl delegate;

        @Override
        @Transactional
        public void deleteLabel(Long labelId, Long currentUserId) {
            Label label = labelPersistencePort.findById(labelId)
                    .orElseThrow(() -> new IllegalArgumentException("Label not found with id: " + labelId));

            Long workspaceId = label.getWorkspace().getId();

            if (auditInterceptor.shouldAudit(workspaceId)) {
                auditInterceptor.auditDelete(
                        workspaceId,
                        currentUserId,
                        AuditEntityType.LABEL,
                        labelId,
                        Map.of("name", label.getName(), "color", label.getColor())
                );
            }

            delegate.deleteLabel(labelId, currentUserId);
        }
    }
}
