package com.afadhitya.taskmanagement.application.port.in.project;

import com.afadhitya.taskmanagement.application.dto.request.UpdateProjectMemberRoleRequest;
import com.afadhitya.taskmanagement.application.dto.response.ProjectMemberResponse;

public interface UpdateProjectMemberRoleUseCase {

    ProjectMemberResponse updateMemberRole(Long projectId, Long userId, UpdateProjectMemberRoleRequest request, Long currentUserId);
}
