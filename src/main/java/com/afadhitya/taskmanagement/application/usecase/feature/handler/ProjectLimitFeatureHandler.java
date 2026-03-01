package com.afadhitya.taskmanagement.application.usecase.feature.handler;

import com.afadhitya.taskmanagement.application.dto.request.CreateProjectRequest;
import com.afadhitya.taskmanagement.application.dto.response.ProjectResponse;
import com.afadhitya.taskmanagement.application.port.feature.FeatureContext;
import com.afadhitya.taskmanagement.application.port.feature.FeatureHandler;
import com.afadhitya.taskmanagement.application.port.out.feature.FeatureTogglePort;
import com.afadhitya.taskmanagement.application.port.out.workspace.WorkspacePersistencePort;
import com.afadhitya.taskmanagement.domain.exception.PlanLimitExceededException;
import com.afadhitya.taskmanagement.domain.feature.Feature;
import com.afadhitya.taskmanagement.domain.feature.LimitType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectLimitFeatureHandler implements FeatureHandler<CreateProjectRequest, ProjectResponse> {

    private final WorkspacePersistencePort workspacePersistencePort;
    private final FeatureTogglePort featureTogglePort;

    @Override
    public Feature getFeature() {
        return Feature.PROJECT_LIMITS;
    }

    @Override
    public void validate(FeatureContext context, CreateProjectRequest request) {
        Long workspaceId = request.workspaceId();

        int currentProjectCount = workspacePersistencePort.countProjects(workspaceId);
        int maxAllowed = featureTogglePort.getLimit(workspaceId, LimitType.MAX_PROJECTS);

        if (maxAllowed >= 0 && currentProjectCount >= maxAllowed) {
            throw new PlanLimitExceededException(
                LimitType.MAX_PROJECTS,
                currentProjectCount,
                maxAllowed
            );
        }
    }
}
