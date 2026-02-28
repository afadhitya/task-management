package com.afadhitya.taskmanagement.application.port.in.comment;

public interface DeleteCommentUseCase {

    void deleteComment(Long commentId, Long currentUserId);
}
