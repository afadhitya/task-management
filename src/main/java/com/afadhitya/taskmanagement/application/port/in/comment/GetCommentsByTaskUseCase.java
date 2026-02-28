package com.afadhitya.taskmanagement.application.port.in.comment;

import com.afadhitya.taskmanagement.application.dto.response.CommentResponse;

import java.util.List;

public interface GetCommentsByTaskUseCase {

    List<CommentResponse> getCommentsByTask(Long taskId);
}
