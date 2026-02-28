package com.afadhitya.taskmanagement.application.mapper;

import com.afadhitya.taskmanagement.application.dto.response.CommentResponse;
import com.afadhitya.taskmanagement.domain.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "taskId", source = "task.id")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorName", source = "author.fullName")
    @Mapping(target = "parentCommentId", source = "parentComment.id")
    @Mapping(target = "replies", expression = "java(mapReplies(comment.getReplies()))")
    CommentResponse toResponse(Comment comment);

    default List<CommentResponse> mapReplies(List<Comment> replies) {
        if (replies == null || replies.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return replies.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
