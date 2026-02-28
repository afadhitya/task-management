package com.afadhitya.taskmanagement.application.usecase.comment;

import com.afadhitya.taskmanagement.application.dto.request.UpdateCommentRequest;
import com.afadhitya.taskmanagement.application.dto.response.CommentResponse;
import com.afadhitya.taskmanagement.application.mapper.CommentMapper;
import com.afadhitya.taskmanagement.application.port.in.comment.UpdateCommentUseCase;
import com.afadhitya.taskmanagement.application.port.out.comment.CommentPersistencePort;
import com.afadhitya.taskmanagement.application.service.AuditEventPublisher;
import com.afadhitya.taskmanagement.domain.entity.Comment;
import com.afadhitya.taskmanagement.domain.enums.AuditEntityType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateCommentUseCaseImpl implements UpdateCommentUseCase {

    private final CommentPersistencePort commentPersistencePort;
    private final CommentMapper commentMapper;
    private final AuditEventPublisher auditEventPublisher;

    @Override
    public CommentResponse updateComment(Long commentId, UpdateCommentRequest request, Long currentUserId) {
        Comment comment = commentPersistencePort.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + commentId));

        commentMapper.updateEntityFromRequest(request, comment);
        Comment updatedComment = commentPersistencePort.save(comment);

        Long workspaceId = updatedComment.getTask().getProject().getWorkspace().getId();
        auditEventPublisher.publishUpdate(
                workspaceId,
                currentUserId,
                AuditEntityType.COMMENT,
                commentId,
                Map.of("taskId", updatedComment.getTask().getId())
        );

        return commentMapper.toResponse(updatedComment);
    }
}
