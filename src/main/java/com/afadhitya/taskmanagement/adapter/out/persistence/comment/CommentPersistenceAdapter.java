package com.afadhitya.taskmanagement.adapter.out.persistence.comment;

import com.afadhitya.taskmanagement.adapter.out.persistence.CommentRepository;
import com.afadhitya.taskmanagement.application.port.out.comment.CommentPersistencePort;
import com.afadhitya.taskmanagement.domain.entity.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommentPersistenceAdapter implements CommentPersistencePort {

    private final CommentRepository commentRepository;

    @Override
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }

    @Override
    public List<Comment> findByTaskIdAndParentCommentIsNullAndIsDeletedFalse(Long taskId) {
        return commentRepository.findByTaskIdAndParentCommentIsNullAndIsDeletedFalse(taskId);
    }

    @Override
    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }
}
