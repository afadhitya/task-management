package com.afadhitya.taskmanagement.application.service;

import com.afadhitya.taskmanagement.application.event.EntityChangedEvent;
import com.afadhitya.taskmanagement.domain.enums.AuditAction;
import com.afadhitya.taskmanagement.domain.enums.AuditEntityType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publish(
            Long workspaceId,
            Long actorId,
            AuditEntityType entityType,
            Long entityId,
            AuditAction action,
            Map<String, Object> diff) {
        
        log.debug("Publishing audit event: workspaceId={}, entityType={}, action={}", 
                workspaceId, entityType, action);
        
        EntityChangedEvent event = EntityChangedEvent.builder()
                .workspaceId(workspaceId)
                .actorId(actorId)
                .entityType(entityType)
                .entityId(entityId)
                .action(action)
                .diff(diff)
                .build();
        
        eventPublisher.publishEvent(event);
    }

    public void publishCreate(
            Long workspaceId,
            Long actorId,
            AuditEntityType entityType,
            Long entityId,
            Map<String, Object> newValues) {
        publish(workspaceId, actorId, entityType, entityId, AuditAction.CREATE, newValues);
    }

    public void publishUpdate(
            Long workspaceId,
            Long actorId,
            AuditEntityType entityType,
            Long entityId,
            Map<String, Object> diff) {
        publish(workspaceId, actorId, entityType, entityId, AuditAction.UPDATE, diff);
    }

    public void publishDelete(
            Long workspaceId,
            Long actorId,
            AuditEntityType entityType,
            Long entityId,
            Map<String, Object> oldValues) {
        publish(workspaceId, actorId, entityType, entityId, AuditAction.DELETE, oldValues);
    }
}
