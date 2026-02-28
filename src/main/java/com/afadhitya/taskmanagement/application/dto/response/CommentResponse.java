package com.afadhitya.taskmanagement.application.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CommentResponse(
        Long id,
        String body,
        Boolean isDeleted,
        Long taskId,
        Long authorId,
        String authorName,
        Long parentCommentId,
        List<CommentResponse> replies,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
