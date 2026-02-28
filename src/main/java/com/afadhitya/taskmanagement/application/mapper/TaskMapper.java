package com.afadhitya.taskmanagement.application.mapper;

import com.afadhitya.taskmanagement.application.dto.request.UpdateTaskRequest;
import com.afadhitya.taskmanagement.application.dto.response.LabelSummaryResponse;
import com.afadhitya.taskmanagement.application.dto.response.TaskResponse;
import com.afadhitya.taskmanagement.domain.entity.Task;
import com.afadhitya.taskmanagement.domain.entity.TaskLabel;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "projectName", source = "project.name")
    @Mapping(target = "parentTaskId", source = "parentTask.id")
    @Mapping(target = "createdBy", source = "createdBy.id")
    @Mapping(target = "createdByName", source = "createdBy.fullName")
    @Mapping(target = "labels", source = "taskLabels")
    TaskResponse toResponse(Task task);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateTaskRequest request, @MappingTarget Task task);

    @Mapping(target = "id", source = "label.id")
    @Mapping(target = "name", source = "label.name")
    @Mapping(target = "color", source = "label.color")
    LabelSummaryResponse toLabelSummaryResponse(TaskLabel taskLabel);

    List<LabelSummaryResponse> toLabelSummaryResponseList(List<TaskLabel> taskLabels);
}
