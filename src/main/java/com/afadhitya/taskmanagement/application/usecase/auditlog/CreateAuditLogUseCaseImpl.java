package com.afadhitya.taskmanagement.application.usecase.auditlog;

import com.afadhitya.taskmanagement.application.dto.request.CreateAuditLogRequest;
import com.afadhitya.taskmanagement.application.port.in.auditlog.CreateAuditLogUseCase;
import com.afadhitya.taskmanagement.application.port.out.auditlog.AuditLogPersistencePort;
import com.afadhitya.taskmanagement.application.port.out.user.UserPersistencePort;
import com.afadhitya.taskmanagement.application.port.out.workspace.WorkspacePersistencePort;
import com.afadhitya.taskmanagement.domain.entity.AuditLog;
import com.afadhitya.taskmanagement.domain.entity.User;
import com.afadhitya.taskmanagement.domain.entity.Workspace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateAuditLogUseCaseImpl implements CreateAuditLogUseCase {

    private final AuditLogPersistencePort auditLogPersistencePort;
    private final WorkspacePersistencePort workspacePersistencePort;
    private final UserPersistencePort userPersistencePort;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void create(CreateAuditLogRequest request) {
        log.debug("Creating audit log: {}", request);

        Workspace workspace = workspacePersistencePort.findById(request.workspaceId())
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found: " + request.workspaceId()));

        User actor = userPersistencePort.findById(request.actorId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.actorId()));

        AuditLog auditLog = AuditLog.builder()
                .workspace(workspace)
                .actor(actor)
                .entityType(request.entityType().name())
                .entityId(request.entityId())
                .action(request.action().name())
                .diff(request.diff())
                .build();

        auditLogPersistencePort.save(auditLog);
        log.debug("Audit log created successfully");
    }
}
