package com.afadhitya.taskmanagement.application.usecase.feature;

import com.afadhitya.taskmanagement.application.dto.request.CreateProjectRequest;
import com.afadhitya.taskmanagement.application.dto.response.ProjectResponse;
import com.afadhitya.taskmanagement.application.port.feature.FeatureContext;
import com.afadhitya.taskmanagement.application.port.feature.FeatureHandler;
import com.afadhitya.taskmanagement.application.port.in.project.CreateProjectUseCase;
import com.afadhitya.taskmanagement.application.port.out.feature.FeatureTogglePort;
import com.afadhitya.taskmanagement.application.usecase.audit.AuditedProjectUseCases;
import com.afadhitya.taskmanagement.domain.feature.Feature;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Primary
public class CreateProjectFeatureDispatcher implements CreateProjectUseCase {

    private final AuditedProjectUseCases.CreateProject auditedCreateProject;
    private final FeatureTogglePort featureTogglePort;
    private final Map<Feature, FeatureHandler<CreateProjectRequest, ProjectResponse>> handlerMap;

    public CreateProjectFeatureDispatcher(AuditedProjectUseCases.CreateProject auditedCreateProject,
                                          FeatureTogglePort featureTogglePort,
                                          List<FeatureHandler<CreateProjectRequest, ProjectResponse>> handlers) {
        this.auditedCreateProject = auditedCreateProject;
        this.featureTogglePort = featureTogglePort;
        this.handlerMap = new EnumMap<>(Feature.class);
        handlers.forEach(h -> handlerMap.put(h.getFeature(), h));
    }

    @Override
    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request, Long createdByUserId) {
        Long workspaceId = request.workspaceId();
        FeatureContext context = new FeatureContext(workspaceId, createdByUserId);

        Map<Feature, Boolean> enabledFeatures = checkFeatures(workspaceId);

        try {
            if (enabledFeatures.getOrDefault(Feature.PROJECT_LIMITS, false)) {
                executeValidate(Feature.PROJECT_LIMITS, context, request, handlerMap);
            }

            return auditedCreateProject.createProject(request, createdByUserId);
        } catch (Exception e) {
            context.setExecutionFailed(true);
            executeOnError(context, request, e, handlerMap);
            throw e;
        }
    }

    private Map<Feature, Boolean> checkFeatures(Long workspaceId) {
        Map<Feature, Boolean> result = new EnumMap<>(Feature.class);
        result.put(Feature.PROJECT_LIMITS, featureTogglePort.isEnabled(workspaceId, Feature.PROJECT_LIMITS));
        result.put(Feature.AUDIT_LOG, featureTogglePort.isEnabled(workspaceId, Feature.AUDIT_LOG));
        return result;
    }

    private void executeValidate(Feature feature, FeatureContext context, CreateProjectRequest request,
            Map<Feature, FeatureHandler<CreateProjectRequest, ProjectResponse>> handlerMap) {
        FeatureHandler<CreateProjectRequest, ProjectResponse> handler = handlerMap.get(feature);
        if (handler != null) {
            log.debug("Executing VALIDATE for feature: {}", feature);
            handler.validate(context, request);
        }
    }

    private void executeOnError(FeatureContext context, CreateProjectRequest request, Exception e,
            Map<Feature, FeatureHandler<CreateProjectRequest, ProjectResponse>> handlerMap) {
        handlerMap.values().forEach(handler -> {
            try {
                handler.onError(context, request, e);
            } catch (Exception suppressed) {
                log.error("Error handler failed for feature: {}", handler.getFeature(), suppressed);
            }
        });
    }
}
