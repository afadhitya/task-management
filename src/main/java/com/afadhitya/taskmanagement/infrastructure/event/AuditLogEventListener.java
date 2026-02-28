package com.afadhitya.taskmanagement.infrastructure.event;

import com.afadhitya.taskmanagement.application.dto.request.CreateAuditLogRequest;
import com.afadhitya.taskmanagement.application.event.EntityChangedEvent;
import com.afadhitya.taskmanagement.application.port.in.auditlog.CreateAuditLogUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogEventListener {

    private final CreateAuditLogUseCase createAuditLogUseCase;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEntityChanged(EntityChangedEvent event) {
        log.debug("Handling entity changed event: {}", event);
        try {
            CreateAuditLogRequest request = CreateAuditLogRequest.builder()
                    .workspaceId(event.getWorkspaceId())
                    .actorId(event.getActorId())
                    .entityType(event.getEntityType())
                    .entityId(event.getEntityId())
                    .action(event.getAction())
                    .diff(event.getDiff())
                    .build();
            createAuditLogUseCase.create(request);
        } catch (Exception e) {
            log.error("Failed to create audit log for event: {}", event, e);
        }
    }
}
