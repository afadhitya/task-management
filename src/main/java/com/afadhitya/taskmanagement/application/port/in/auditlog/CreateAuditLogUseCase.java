package com.afadhitya.taskmanagement.application.port.in.auditlog;

import com.afadhitya.taskmanagement.application.dto.request.CreateAuditLogRequest;

public interface CreateAuditLogUseCase {

    void create(CreateAuditLogRequest request);
}
