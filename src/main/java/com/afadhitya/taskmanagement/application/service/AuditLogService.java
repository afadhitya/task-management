package com.afadhitya.taskmanagement.application.service;

import com.afadhitya.taskmanagement.application.port.out.auditlog.AuditLogPersistencePort;
import com.afadhitya.taskmanagement.application.port.out.user.UserPersistencePort;
import com.afadhitya.taskmanagement.application.port.out.workspace.WorkspacePersistencePort;
import com.afadhitya.taskmanagement.domain.entity.AuditLog;
import com.afadhitya.taskmanagement.domain.entity.User;
import com.afadhitya.taskmanagement.domain.entity.Workspace;
import com.afadhitya.taskmanagement.domain.enums.AuditAction;
import com.afadhitya.taskmanagement.domain.enums.AuditEntityType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private final AuditLogPersistencePort auditLogPersistencePort;
    private final WorkspacePersistencePort workspacePersistencePort;
    private final UserPersistencePort userPersistencePort;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void create(
            Long workspaceId,
            Long actorId,
            AuditEntityType entityType,
            Long entityId,
            AuditAction action,
            Map<String, Object> diff) {
        
        log.debug("Creating audit log: workspaceId={}, entityType={}, action={}", 
                workspaceId, entityType, action);

        Workspace workspace = workspacePersistencePort.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found: " + workspaceId));

        User actor = userPersistencePort.findById(actorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + actorId));

        AuditLog auditLog = AuditLog.builder()
                .workspace(workspace)
                .actor(actor)
                .entityType(entityType.name())
                .entityId(entityId)
                .action(action.name())
                .diff(diff)
                .build();

        auditLogPersistencePort.save(auditLog);
        log.debug("Audit log created successfully");
    }

    public void createCreate(
            Long workspaceId,
            Long actorId,
            AuditEntityType entityType,
            Long entityId,
            Map<String, Object> newValues) {
        create(workspaceId, actorId, entityType, entityId, AuditAction.CREATE, newValues);
    }

    public void createUpdate(
            Long workspaceId,
            Long actorId,
            AuditEntityType entityType,
            Long entityId,
            Map<String, Object> diff) {
        create(workspaceId, actorId, entityType, entityId, AuditAction.UPDATE, diff);
    }

    public void createDelete(
            Long workspaceId,
            Long actorId,
            AuditEntityType entityType,
            Long entityId,
            Map<String, Object> oldValues) {
        create(workspaceId, actorId, entityType, entityId, AuditAction.DELETE, oldValues);
    }
}
