package com.afadhitya.taskmanagement.application.usecase.feature;

import com.afadhitya.taskmanagement.application.port.out.feature.FeatureTogglePort;
import com.afadhitya.taskmanagement.application.service.AuditLogService;
import com.afadhitya.taskmanagement.domain.enums.AuditAction;
import com.afadhitya.taskmanagement.domain.enums.AuditEntityType;
import com.afadhitya.taskmanagement.domain.feature.Feature;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditFeatureInterceptor {

    private final FeatureTogglePort featureToggle;
    private final AuditLogService auditLogService;

    public boolean shouldAudit(Long workspaceId) {
        boolean enabled = featureToggle.isEnabled(workspaceId, Feature.AUDIT_LOG);
        log.debug("Audit feature check for workspace {}: {}", workspaceId, enabled);
        return enabled;
    }

    public void audit(Long workspaceId, Long actorId, AuditEntityType entityType,
                      Long entityId, AuditAction action, Map<String, Object> data) {
        if (!shouldAudit(workspaceId)) {
            log.debug("Skipping audit log for {} {} - feature disabled", entityType, entityId);
            return;
        }

        auditLogService.create(workspaceId, actorId, entityType, entityId, action, data);
        log.debug("Created audit log for {} {} with action {}", entityType, entityId, action);
    }

    public void auditCreate(Long workspaceId, Long actorId, AuditEntityType entityType,
                            Long entityId, Map<String, Object> newValues) {
        audit(workspaceId, actorId, entityType, entityId, AuditAction.CREATE, newValues);
    }

    public void auditUpdate(Long workspaceId, Long actorId, AuditEntityType entityType,
                            Long entityId, Map<String, Object> diff) {
        if (diff == null || diff.isEmpty()) {
            return;
        }
        AuditAction action = diff.containsKey("status") ? AuditAction.STATUS_CHANGE : AuditAction.UPDATE;
        audit(workspaceId, actorId, entityType, entityId, action, diff);
    }

    public void auditDelete(Long workspaceId, Long actorId, AuditEntityType entityType,
                            Long entityId, Map<String, Object> deletedData) {
        audit(workspaceId, actorId, entityType, entityId, AuditAction.DELETE, deletedData);
    }
}
