package com.afadhitya.taskmanagement.application.usecase.audit;

import com.afadhitya.taskmanagement.application.dto.request.CreateCommentRequest;
import com.afadhitya.taskmanagement.application.dto.request.UpdateCommentRequest;
import com.afadhitya.taskmanagement.application.dto.response.CommentResponse;
import com.afadhitya.taskmanagement.application.port.in.comment.CreateCommentUseCase;
import com.afadhitya.taskmanagement.application.port.in.comment.DeleteCommentUseCase;
import com.afadhitya.taskmanagement.application.port.in.comment.UpdateCommentUseCase;
import com.afadhitya.taskmanagement.application.port.out.comment.CommentPersistencePort;
import com.afadhitya.taskmanagement.application.service.AuditLogService;
import com.afadhitya.taskmanagement.application.usecase.comment.CreateCommentUseCaseImpl;
import com.afadhitya.taskmanagement.application.usecase.comment.DeleteCommentUseCaseImpl;
import com.afadhitya.taskmanagement.application.usecase.comment.UpdateCommentUseCaseImpl;
import com.afadhitya.taskmanagement.application.usecase.feature.AuditFeatureInterceptor;
import com.afadhitya.taskmanagement.domain.entity.Comment;
import com.afadhitya.taskmanagement.domain.enums.AuditEntityType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Primary
@RequiredArgsConstructor
public class AuditedCommentUseCases {

    private final CommentPersistencePort commentPersistencePort;
    private final AuditLogService auditLogService;
    private final AuditFeatureInterceptor auditInterceptor;

    @Service
    @Primary
    @RequiredArgsConstructor
    public class CreateComment implements CreateCommentUseCase {

        private final CreateCommentUseCaseImpl delegate;

        @Override
        @Transactional
        public CommentResponse createComment(Long taskId, CreateCommentRequest request, Long authorId) {
            CommentResponse response = delegate.createComment(taskId, request, authorId);

            Comment comment = commentPersistencePort.findById(response.id()).orElseThrow();
            Long workspaceId = comment.getTask().getProject().getWorkspace().getId();

            if (!auditInterceptor.shouldAudit(workspaceId)) {
                return response;
            }

            auditInterceptor.auditCreate(
                    workspaceId,
                    authorId,
                    AuditEntityType.COMMENT,
                    response.id(),
                    Map.of("taskId", taskId, "bodyPreview", response.body().substring(0, Math.min(100, response.body().length())))
            );

            return response;
        }
    }

    @Service
    @Primary
    @RequiredArgsConstructor
    public class UpdateComment implements UpdateCommentUseCase {

        private final UpdateCommentUseCaseImpl delegate;

        @Override
        @Transactional
        public CommentResponse updateComment(Long commentId, UpdateCommentRequest request, Long currentUserId) {
            CommentResponse response = delegate.updateComment(commentId, request, currentUserId);

            Comment comment = commentPersistencePort.findById(response.id()).orElseThrow();
            Long workspaceId = comment.getTask().getProject().getWorkspace().getId();

            if (!auditInterceptor.shouldAudit(workspaceId)) {
                return response;
            }

            auditInterceptor.auditUpdate(
                    workspaceId,
                    currentUserId,
                    AuditEntityType.COMMENT,
                    commentId,
                    Map.of("taskId", comment.getTask().getId())
            );

            return response;
        }
    }

    @Service
    @Primary
    @RequiredArgsConstructor
    public class DeleteComment implements DeleteCommentUseCase {

        private final DeleteCommentUseCaseImpl delegate;

        @Override
        @Transactional
        public void deleteComment(Long commentId, Long currentUserId) {
            Comment comment = commentPersistencePort.findById(commentId)
                    .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + commentId));

            Long workspaceId = comment.getTask().getProject().getWorkspace().getId();

            if (auditInterceptor.shouldAudit(workspaceId)) {
                auditInterceptor.auditDelete(
                        workspaceId,
                        currentUserId,
                        AuditEntityType.COMMENT,
                        commentId,
                        Map.of("taskId", comment.getTask().getId())
                );
            }

            delegate.deleteComment(commentId, currentUserId);
        }
    }
}
