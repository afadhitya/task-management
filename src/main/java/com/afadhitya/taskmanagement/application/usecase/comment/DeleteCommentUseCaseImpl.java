package com.afadhitya.taskmanagement.application.usecase.comment;

import com.afadhitya.taskmanagement.application.port.in.comment.DeleteCommentUseCase;
import com.afadhitya.taskmanagement.application.port.in.project.ProjectPermissionUseCase;
import com.afadhitya.taskmanagement.application.port.out.comment.CommentPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.Comment;
import com.afadhitya.taskmanagement.domain.enums.ProjectPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteCommentUseCaseImpl implements DeleteCommentUseCase {

    private final CommentPersistencePort commentPersistencePort;
    private final ProjectPermissionUseCase projectPermissionUseCase;

    @Override
    public void deleteComment(Long commentId, Long currentUserId) {
        Comment comment = commentPersistencePort.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + commentId));

        Long projectId = comment.getTask().getProject().getId();

        // Check permission: author or project manager
        boolean isAuthor = comment.getAuthor().getId().equals(currentUserId);
        boolean isProjectManager = projectPermissionUseCase.hasPermission(
                projectId, currentUserId, ProjectPermission.MANAGER);

        if (!isAuthor && !isProjectManager) {
            throw new IllegalStateException("You don't have permission to delete this comment. Only the author or project manager can delete it.");
        }

        commentPersistencePort.deleteById(commentId);
    }
}
