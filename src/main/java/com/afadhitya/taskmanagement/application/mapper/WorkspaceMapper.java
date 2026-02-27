package com.afadhitya.taskmanagement.application.mapper;

import com.afadhitya.taskmanagement.application.dto.request.CreateWorkspaceRequest;
import com.afadhitya.taskmanagement.application.dto.request.UpdateWorkspaceRequest;
import com.afadhitya.taskmanagement.application.dto.response.WorkspaceResponse;
import com.afadhitya.taskmanagement.domain.entity.Workspace;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface WorkspaceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "planTier", constant = "FREE")
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "labels", ignore = true)
    @Mapping(target = "auditLogs", ignore = true)
    Workspace toEntity(CreateWorkspaceRequest request);

    @Mapping(target = "ownerId", source = "owner.id")
    WorkspaceResponse toResponse(Workspace workspace);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "planTier", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "labels", ignore = true)
    @Mapping(target = "auditLogs", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateWorkspaceRequest request, @MappingTarget Workspace workspace);
}
