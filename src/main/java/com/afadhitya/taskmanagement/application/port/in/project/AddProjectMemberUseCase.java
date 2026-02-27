package com.afadhitya.taskmanagement.application.port.in.project;

import com.afadhitya.taskmanagement.application.dto.request.AddProjectMemberRequest;
import com.afadhitya.taskmanagement.application.dto.response.ProjectMemberResponse;

public interface AddProjectMemberUseCase {

    ProjectMemberResponse addMember(Long projectId, AddProjectMemberRequest request);
}
