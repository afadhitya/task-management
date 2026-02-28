package com.afadhitya.taskmanagement.application.event;

import com.afadhitya.taskmanagement.domain.enums.AuditAction;
import com.afadhitya.taskmanagement.domain.enums.AuditEntityType;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class EntityChangedEvent {

    private final Long workspaceId;
    private final Long actorId;
    private final AuditEntityType entityType;
    private final Long entityId;
    private final AuditAction action;
    private final Map<String, Object> diff;
}
