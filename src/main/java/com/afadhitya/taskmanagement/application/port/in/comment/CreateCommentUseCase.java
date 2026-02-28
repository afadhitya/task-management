package com.afadhitya.taskmanagement.application.port.in.comment;

import com.afadhitya.taskmanagement.application.dto.request.CreateCommentRequest;
import com.afadhitya.taskmanagement.application.dto.response.CommentResponse;

public interface CreateCommentUseCase {

    CommentResponse createComment(Long taskId, CreateCommentRequest request, Long authorId);
}
