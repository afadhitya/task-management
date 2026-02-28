package com.afadhitya.taskmanagement.application.usecase.comment;

import com.afadhitya.taskmanagement.application.port.in.comment.DeleteCommentUseCase;
import com.afadhitya.taskmanagement.application.port.out.comment.CommentPersistencePort;
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
        commentPersistencePort.deleteById(commentId);
    }
}
