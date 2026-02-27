package com.afadhitya.taskmanagement.application.port.in.project;

import com.afadhitya.taskmanagement.application.dto.response.ProjectResponse;

import java.util.List;

public interface GetProjectsByWorkspaceUseCase {

    List<ProjectResponse> getProjectsByWorkspace(Long workspaceId);
}
