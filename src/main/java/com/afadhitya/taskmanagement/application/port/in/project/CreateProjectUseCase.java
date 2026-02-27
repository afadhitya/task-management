package com.afadhitya.taskmanagement.application.port.in.project;

import com.afadhitya.taskmanagement.application.dto.request.CreateProjectRequest;
import com.afadhitya.taskmanagement.application.dto.response.ProjectResponse;

public interface CreateProjectUseCase {

    ProjectResponse createProject(CreateProjectRequest request, Long createdByUserId);
}
