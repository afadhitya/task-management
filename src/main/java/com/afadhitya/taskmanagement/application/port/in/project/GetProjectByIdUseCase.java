package com.afadhitya.taskmanagement.application.port.in.project;

import com.afadhitya.taskmanagement.application.dto.response.ProjectResponse;

public interface GetProjectByIdUseCase {

    ProjectResponse getProjectById(Long id);
}
