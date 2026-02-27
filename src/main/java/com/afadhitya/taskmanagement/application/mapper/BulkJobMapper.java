package com.afadhitya.taskmanagement.application.mapper;

import com.afadhitya.taskmanagement.application.dto.response.BulkJobResponse;
import com.afadhitya.taskmanagement.domain.entity.BulkJob;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BulkJobMapper {

    @Mapping(target = "createdBy", source = "createdBy.id")
    @Mapping(target = "progressPercentage", expression = "java(bulkJob.getProgressPercentage())")
    BulkJobResponse toResponse(BulkJob bulkJob);
}
