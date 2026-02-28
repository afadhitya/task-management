package com.afadhitya.taskmanagement.application.usecase.workspace;

import com.afadhitya.taskmanagement.application.dto.request.CreateWorkspaceRequest;
import com.afadhitya.taskmanagement.application.dto.response.WorkspaceResponse;
import com.afadhitya.taskmanagement.application.mapper.WorkspaceMapper;
import com.afadhitya.taskmanagement.application.port.in.workspace.CreateWorkspaceUseCase;
import com.afadhitya.taskmanagement.application.port.out.user.UserPersistencePort;
import com.afadhitya.taskmanagement.application.port.out.workspace.WorkspaceMemberPersistencePort;
import com.afadhitya.taskmanagement.application.port.out.workspace.WorkspacePersistencePort;
import com.afadhitya.taskmanagement.application.service.AuditEventPublisher;
import com.afadhitya.taskmanagement.domain.entity.User;
import com.afadhitya.taskmanagement.domain.entity.Workspace;
import com.afadhitya.taskmanagement.domain.entity.WorkspaceMember;
import com.afadhitya.taskmanagement.domain.enums.AuditEntityType;
import com.afadhitya.taskmanagement.domain.enums.WorkspaceRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateWorkspaceUseCaseImpl implements CreateWorkspaceUseCase {

    private final WorkspacePersistencePort workspacePersistencePort;
    private final UserPersistencePort userPersistencePort;
    private final WorkspaceMemberPersistencePort workspaceMemberPersistencePort;
    private final WorkspaceMapper workspaceMapper;
    private final AuditEventPublisher auditEventPublisher;

    @Override
    public WorkspaceResponse createWorkspace(CreateWorkspaceRequest request, Long ownerId) {
        if (workspacePersistencePort.existsBySlug(request.slug())) {
            throw new IllegalArgumentException("Slug already exists: " + request.slug());
        }

        User owner = userPersistencePort.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found with id: " + ownerId));

        Workspace workspace = workspaceMapper.toEntity(request);
        workspace.setOwner(owner);

        Workspace savedWorkspace = workspacePersistencePort.save(workspace);

        // Add creator as workspace member with OWNER role
        WorkspaceMember ownerMember = WorkspaceMember.builder()
                .workspace(savedWorkspace)
                .user(owner)
                .role(WorkspaceRole.OWNER)
                .invitedBy(owner)
                .build();
        workspaceMemberPersistencePort.save(ownerMember);

        auditEventPublisher.publishCreate(
                savedWorkspace.getId(),
                ownerId,
                AuditEntityType.WORKSPACE,
                savedWorkspace.getId(),
                Map.of("name", savedWorkspace.getName(), "slug", savedWorkspace.getSlug())
        );

        return workspaceMapper.toResponse(savedWorkspace);
    }
}
