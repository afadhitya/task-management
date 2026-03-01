package com.afadhitya.taskmanagement.application.usecase.feature;

import com.afadhitya.taskmanagement.application.dto.request.InviteMemberRequest;
import com.afadhitya.taskmanagement.application.dto.response.WorkspaceMemberResponse;
import com.afadhitya.taskmanagement.application.port.feature.FeatureContext;
import com.afadhitya.taskmanagement.application.port.feature.FeatureHandler;
import com.afadhitya.taskmanagement.application.port.in.workspace.InviteMemberUseCase;
import com.afadhitya.taskmanagement.application.port.out.feature.FeatureTogglePort;
import com.afadhitya.taskmanagement.application.usecase.audit.AuditedWorkspaceUseCases;
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
public class InviteMemberFeatureDispatcher implements InviteMemberUseCase {

    private final AuditedWorkspaceUseCases.InviteMember auditedInviteMember;
    private final FeatureTogglePort featureTogglePort;
    private final Map<Feature, FeatureHandler<InviteMemberRequest, WorkspaceMemberResponse>> handlerMap;

    public InviteMemberFeatureDispatcher(AuditedWorkspaceUseCases.InviteMember auditedInviteMember,
                                         FeatureTogglePort featureTogglePort,
                                         List<FeatureHandler<InviteMemberRequest, WorkspaceMemberResponse>> handlers,
                                         Map<Feature, FeatureHandler<InviteMemberRequest, WorkspaceMemberResponse>> handlerMap) {
        this.auditedInviteMember = auditedInviteMember;
        this.featureTogglePort = featureTogglePort;
        this.handlerMap = new EnumMap<>(Feature.class);
        handlers.forEach(h -> handlerMap.put(h.getFeature(), h));
    }

    @Override
    @Transactional
    public WorkspaceMemberResponse inviteMember(Long workspaceId, InviteMemberRequest request, Long currentUserId) {
        FeatureContext context = new FeatureContext(workspaceId, currentUserId);

        Map<Feature, Boolean> enabledFeatures = checkFeatures(workspaceId);

        try {
            if (enabledFeatures.getOrDefault(Feature.MEMBER_LIMITS, false)) {
                executeValidate(Feature.MEMBER_LIMITS, context, request, handlerMap);
            }

            return auditedInviteMember.inviteMember(workspaceId, request, currentUserId);
        } catch (Exception e) {
            context.setExecutionFailed(true);
            executeOnError(context, request, e, handlerMap);
            throw e;
        }
    }

    private Map<Feature, Boolean> checkFeatures(Long workspaceId) {
        Map<Feature, Boolean> result = new EnumMap<>(Feature.class);
        result.put(Feature.MEMBER_LIMITS, featureTogglePort.isEnabled(workspaceId, Feature.MEMBER_LIMITS));
        result.put(Feature.AUDIT_LOG, featureTogglePort.isEnabled(workspaceId, Feature.AUDIT_LOG));
        return result;
    }

    private void executeValidate(Feature feature, FeatureContext context, InviteMemberRequest request,
            Map<Feature, FeatureHandler<InviteMemberRequest, WorkspaceMemberResponse>> handlerMap) {
        FeatureHandler<InviteMemberRequest, WorkspaceMemberResponse> handler = handlerMap.get(feature);
        if (handler != null) {
            log.debug("Executing VALIDATE for feature: {}", feature);
            handler.validate(context, request);
        }
    }

    private void executeOnError(FeatureContext context, InviteMemberRequest request, Exception e,
            Map<Feature, FeatureHandler<InviteMemberRequest, WorkspaceMemberResponse>> handlerMap) {
        handlerMap.values().forEach(handler -> {
            try {
                handler.onError(context, request, e);
            } catch (Exception suppressed) {
                log.error("Error handler failed for feature: {}", handler.getFeature(), suppressed);
            }
        });
    }
}
