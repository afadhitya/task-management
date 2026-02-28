package com.afadhitya.taskmanagement.application.port.out.comment;

import com.afadhitya.taskmanagement.domain.entity.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentPersistencePort {

    Comment save(Comment comment);

    Optional<Comment> findById(Long id);

    List<Comment> findByTaskId(Long taskId);

    List<Comment> findByTaskIdAndParentCommentIsNull(Long taskId);

    void deleteById(Long id);
}
