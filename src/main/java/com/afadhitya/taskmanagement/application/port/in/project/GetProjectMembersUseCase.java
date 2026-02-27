package com.afadhitya.taskmanagement.application.port.in.project;

import com.afadhitya.taskmanagement.application.dto.response.ProjectMemberResponse;

import java.util.List;

public interface GetProjectMembersUseCase {

    List<ProjectMemberResponse> getProjectMembers(Long projectId);
}
