package com.afadhitya.taskmanagement.application.mapper;

import com.afadhitya.taskmanagement.application.dto.response.AuditLogResponse;
import com.afadhitya.taskmanagement.domain.entity.AuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {

    @Mapping(target = "workspaceId", source = "workspace.id")
    @Mapping(target = "actorId", source = "actor.id")
    @Mapping(target = "actorFullName", source = "actor.fullName")
    AuditLogResponse toResponse(AuditLog auditLog);
}
