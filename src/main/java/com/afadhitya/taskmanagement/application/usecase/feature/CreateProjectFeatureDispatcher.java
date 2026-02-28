package com.afadhitya.taskmanagement.application.usecase.feature;

import com.afadhitya.taskmanagement.application.dto.request.CreateProjectRequest;
import com.afadhitya.taskmanagement.application.dto.response.ProjectResponse;
import com.afadhitya.taskmanagement.application.port.feature.FeatureContext;
import com.afadhitya.taskmanagement.application.port.in.project.CreateProjectUseCase;
import com.afadhitya.taskmanagement.application.usecase.audit.AuditedProjectUseCases;
import com.afadhitya.taskmanagement.application.usecase.feature.handler.ProjectLimitFeatureHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Primary
@RequiredArgsConstructor
public class CreateProjectFeatureDispatcher implements CreateProjectUseCase {

    private final AuditedProjectUseCases.CreateProject auditedCreateProject;
    private final ProjectLimitFeatureHandler limitHandler;

    @Override
    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request, Long createdByUserId) {
        Long workspaceId = request.workspaceId();

        FeatureContext context = new FeatureContext(workspaceId, createdByUserId);

        limitHandler.validate(context, request);

        return auditedCreateProject.createProject(request, createdByUserId);
    }
}
