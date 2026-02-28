package com.afadhitya.taskmanagement.application.usecase.comment;

import com.afadhitya.taskmanagement.application.port.in.comment.DeleteCommentUseCase;
import com.afadhitya.taskmanagement.application.port.out.comment.CommentPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeleteCommentUseCaseImpl implements DeleteCommentUseCase {

    private final CommentPersistencePort commentPersistencePort;

    @Override
    public void deleteComment(Long commentId, Long currentUserId) {
        Comment comment = commentPersistencePort.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + commentId));

        comment.setIsDeleted(true);
        commentPersistencePort.save(comment);
    }
}
