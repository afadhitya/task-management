package com.afadhitya.taskmanagement.application.mapper;

import com.afadhitya.taskmanagement.application.dto.request.UpdateTaskRequest;
import com.afadhitya.taskmanagement.application.dto.response.TaskResponse;
import com.afadhitya.taskmanagement.domain.entity.Task;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "projectName", source = "project.name")
    @Mapping(target = "parentTaskId", source = "parentTask.id")
    @Mapping(target = "createdBy", source = "createdBy.id")
    @Mapping(target = "createdByName", source = "createdBy.fullName")
    TaskResponse toResponse(Task task);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateTaskRequest request, @MappingTarget Task task);
}
