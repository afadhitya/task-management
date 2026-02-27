package com.afadhitya.taskmanagement.application.port.in.project;

import com.afadhitya.taskmanagement.application.dto.request.UpdateProjectRequest;
import com.afadhitya.taskmanagement.application.dto.response.ProjectResponse;

public interface UpdateProjectUseCase {

    ProjectResponse updateProject(Long id, UpdateProjectRequest request);
}
