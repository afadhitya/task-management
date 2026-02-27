package com.afadhitya.taskmanagement.application.mapper;

import com.afadhitya.taskmanagement.application.dto.CreateUserRequest;
import com.afadhitya.taskmanagement.application.dto.UpdateUserRequest;
import com.afadhitya.taskmanagement.application.dto.UserResponse;
import com.afadhitya.taskmanagement.domain.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "passwordHash", source = "password")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "workspaceMemberships", ignore = true)
    @Mapping(target = "projectMemberships", ignore = true)
    @Mapping(target = "createdProjects", ignore = true)
    @Mapping(target = "createdTasks", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "notifications", ignore = true)
    @Mapping(target = "auditLogs", ignore = true)
    User toEntity(CreateUserRequest request);

    UserResponse toResponse(User user);

    @Mapping(target = "passwordHash", source = "password")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", source = "email", conditionExpression = "java(request.email() != null)")
    @Mapping(target = "fullName", source = "fullName", conditionExpression = "java(request.fullName() != null)")
    @Mapping(target = "avatarUrl", source = "avatarUrl", conditionExpression = "java(request.avatarUrl() != null)")
    @Mapping(target = "isActive", source = "isActive", conditionExpression = "java(request.isActive() != null)")
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "workspaceMemberships", ignore = true)
    @Mapping(target = "projectMemberships", ignore = true)
    @Mapping(target = "createdProjects", ignore = true)
    @Mapping(target = "createdTasks", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "notifications", ignore = true)
    @Mapping(target = "auditLogs", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateUserRequest request, @MappingTarget User user);
}
