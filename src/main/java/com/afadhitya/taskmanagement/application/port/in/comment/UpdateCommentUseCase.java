package com.afadhitya.taskmanagement.application.port.in.comment;

import com.afadhitya.taskmanagement.application.dto.request.UpdateCommentRequest;
import com.afadhitya.taskmanagement.application.dto.response.CommentResponse;

public interface UpdateCommentUseCase {

    CommentResponse updateComment(Long commentId, UpdateCommentRequest request, Long currentUserId);
}
