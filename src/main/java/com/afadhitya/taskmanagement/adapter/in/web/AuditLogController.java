package com.afadhitya.taskmanagement.adapter.in.web;

import com.afadhitya.taskmanagement.application.dto.response.AuditLogResponse;
import com.afadhitya.taskmanagement.application.port.in.auditlog.ListAuditLogsUseCase;
import com.afadhitya.taskmanagement.infrastructure.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/workspaces")
@RequiredArgsConstructor
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class AuditLogController {

    private final ListAuditLogsUseCase listAuditLogsUseCase;

    @PreAuthorize("@workspaceSecurity.hasWorkspaceRole(#workspaceId, 'OWNER', 'ADMIN')")
    @GetMapping("/{workspaceId}/audit-logs")
    public ResponseEntity<Page<AuditLogResponse>> listAuditLogs(
            @PathVariable Long workspaceId,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AuditLogResponse> auditLogs = listAuditLogsUseCase.listAuditLogs(
                workspaceId, entityType, action, from, to, pageable);
        return ResponseEntity.ok(auditLogs);
    }
}
