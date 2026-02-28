package com.afadhitya.taskmanagement.infrastructure.security;

import com.afadhitya.taskmanagement.application.port.in.project.ProjectPermissionUseCase;
import com.afadhitya.taskmanagement.application.port.out.comment.CommentPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.Comment;
import com.afadhitya.taskmanagement.domain.enums.ProjectPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("commentSecurity")
@RequiredArgsConstructor
public class CommentSecurityExpression {

    private final CommentPersistencePort commentPersistencePort;
    private final ProjectPermissionUseCase projectPermissionUseCase;

    public boolean canModifyComment(Long commentId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        Comment comment = commentPersistencePort.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + commentId));

        // Check if user is the author
        boolean isAuthor = comment.getAuthor().getId().equals(currentUserId);
        if (isAuthor) {
            return true;
        }

        // Check if user is project manager
        Long projectId = comment.getTask().getProject().getId();
        return projectPermissionUseCase.hasPermission(projectId, currentUserId, ProjectPermission.MANAGER);
    }
}
