package com.afadhitya.taskmanagement.application.usecase.comment;

import com.afadhitya.taskmanagement.application.dto.request.CreateCommentRequest;
import com.afadhitya.taskmanagement.application.dto.response.CommentResponse;
import com.afadhitya.taskmanagement.application.mapper.CommentMapper;
import com.afadhitya.taskmanagement.application.port.in.comment.CreateCommentUseCase;
import com.afadhitya.taskmanagement.application.port.out.comment.CommentPersistencePort;
import com.afadhitya.taskmanagement.application.port.out.task.TaskPersistencePort;
import com.afadhitya.taskmanagement.application.port.out.user.UserPersistencePort;
import com.afadhitya.taskmanagement.application.service.AuditEventPublisher;
import com.afadhitya.taskmanagement.domain.entity.Comment;
import com.afadhitya.taskmanagement.domain.entity.Task;
import com.afadhitya.taskmanagement.domain.entity.User;
import com.afadhitya.taskmanagement.domain.enums.AuditEntityType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateCommentUseCaseImpl implements CreateCommentUseCase {

    private final CommentPersistencePort commentPersistencePort;
    private final TaskPersistencePort taskPersistencePort;
    private final UserPersistencePort userPersistencePort;
    private final CommentMapper commentMapper;
    private final AuditEventPublisher auditEventPublisher;

    @Override
    public CommentResponse createComment(Long taskId, CreateCommentRequest request, Long authorId) {
        Task task = taskPersistencePort.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + taskId));

        User author = userPersistencePort.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + authorId));

        Comment.CommentBuilder commentBuilder = Comment.builder()
                .body(request.body())
                .task(task)
                .author(author);

        if (request.parentCommentId() != null) {
            Comment parentComment = commentPersistencePort.findById(request.parentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent comment not found with id: " + request.parentCommentId()));

            // Prevent reply to reply
            if (parentComment.getParentComment() != null) {
                throw new IllegalArgumentException("Cannot reply to a reply. Only one level of nesting is allowed.");
            }
            commentBuilder.parentComment(parentComment);
        }

        Comment comment = commentBuilder.build();
        Comment savedComment = commentPersistencePort.save(comment);

        Long workspaceId = task.getProject().getWorkspace().getId();
        auditEventPublisher.publishCreate(
                workspaceId,
                authorId,
                AuditEntityType.COMMENT,
                savedComment.getId(),
                Map.of("taskId", taskId, "bodyPreview", savedComment.getBody().substring(0, Math.min(100, savedComment.getBody().length())))
        );

        return commentMapper.toResponse(savedComment);
    }
}
