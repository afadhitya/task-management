package com.afadhitya.taskmanagement.application.mapper;

import com.afadhitya.taskmanagement.application.dto.request.UpdateCommentRequest;
import com.afadhitya.taskmanagement.application.dto.response.CommentResponse;
import com.afadhitya.taskmanagement.domain.entity.Comment;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "taskId", source = "task.id")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorName", source = "author.fullName")
    @Mapping(target = "parentCommentId", source = "parentComment.id")
    @Mapping(target = "body", expression = "java(maskDeletedBody(comment))")
    @Mapping(target = "replies", expression = "java(mapReplies(comment.getReplies()))")
    CommentResponse toResponse(Comment comment);

    default String maskDeletedBody(Comment comment) {
        if (Boolean.TRUE.equals(comment.getIsDeleted())) {
            return "[deleted]";
        }
        return comment.getBody();
    }

    default List<CommentResponse> mapReplies(List<Comment> replies) {
        if (replies == null || replies.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return replies.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateCommentRequest request, @MappingTarget Comment comment);
}
