package com.afadhitya.taskmanagement.application.mapper;

import com.afadhitya.taskmanagement.application.dto.request.UpdateLabelRequest;
import com.afadhitya.taskmanagement.application.dto.response.LabelResponse;
import com.afadhitya.taskmanagement.domain.entity.Label;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface LabelMapper {

    @Mapping(target = "workspaceId", source = "workspace.id")
    @Mapping(target = "workspaceName", source = "workspace.name")
    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "projectName", source = "project.name")
    @Mapping(target = "isGlobal", expression = "java(label.isGlobal())")
    @Mapping(target = "createdBy", source = "createdBy.id")
    @Mapping(target = "createdByName", source = "createdBy.fullName")
    LabelResponse toResponse(Label label);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "workspace", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateLabelRequest request, @MappingTarget Label label);
}
